
package io.collapp.service;

import io.collapp.common.DatabaseMigrationDoneEvent;
import io.collapp.common.Json;
import io.collapp.common.collappEnvironment;
import io.collapp.model.Key;
import io.collapp.model.MailConfig;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.MailException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple scheduler. Note: it's not cluster aware.
 */
public class Scheduler implements ApplicationListener<DatabaseMigrationDoneEvent> {

    private static final Logger LOG = LogManager.getLogger();

    private final TaskScheduler taskScheduler;
    private final collappEnvironment env;
    private final ConfigurationRepository configurationRepository;
    private final MySqlFullTextSupportService mySqlFullTextSupportService;
    private final NotificationService notificationService;
    private final StatisticsService statisticsService;
    private final MailTicketService mailTicketService;
    private final ExportImportService exportImportService;


    public Scheduler(TaskScheduler taskScheduler,
                     collappEnvironment env,
                     ConfigurationRepository configurationRepository,
                     MySqlFullTextSupportService mySqlFullTextSupportService,
                     NotificationService notificationService,
                     StatisticsService statisticsService,
                     MailTicketService mailTicketService,
                     ExportImportService exportImportService) {
        this.taskScheduler = taskScheduler;
        this.env = env;
        this.configurationRepository = configurationRepository;
        this.mySqlFullTextSupportService = mySqlFullTextSupportService;
        this.notificationService = notificationService;
        this.statisticsService = statisticsService;
        this.mailTicketService = mailTicketService;
        this.exportImportService = exportImportService;
    }

    @Scheduled(cron = "30 59 23,5,11,17 * * *")
    public void snapshotCardsStatus() {
        if (exportImportService.isImporting()) {
            return;
        }
        statisticsService.snapshotCardsStatus();
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void checkMailTickets() {
        if (exportImportService.isImporting()) {
            return;
        }

        mailTicketService.checkNew();
    }

    private static class EmailNotificationHandler implements Runnable {

        private final ConfigurationRepository configurationRepository;
        private final NotificationService notificationService;
        private final ExportImportService exportImportService;

        private EmailNotificationHandler(ConfigurationRepository configurationRepository,
                                         NotificationService notificationService,
                                         ExportImportService exportImportService) {
            this.configurationRepository = configurationRepository;
            this.notificationService = notificationService;
            this.exportImportService = exportImportService;
        }

        @Override
        public void run() {
            if (exportImportService.isImporting()) {
                return;
            }

            Date upTo = new Date();
            Set<Integer> usersToNotify = notificationService.check(upTo);

            Map<Key, String> conf = configurationRepository.findConfigurationFor(EnumSet.of(Key.SMTP_ENABLED,
                Key.SMTP_CONFIG));

            boolean enabled = Boolean.parseBoolean(ObjectUtils.firstNonNull(conf.get(Key.SMTP_ENABLED), "false"));
            MailConfig mailConfig = Json.GSON.fromJson(conf.get(Key.SMTP_CONFIG), MailConfig.class);
            for (int userId : usersToNotify) {
                try {
                    notificationService.notifyUser(userId, upTo, enabled, mailConfig);
                } catch (MailException me) {
                    LOG.error("Error while sending email to userId " + userId, me);
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(DatabaseMigrationDoneEvent event) {
        if ("MYSQL".equals(env.getProperty("datasource.dialect"))) {
            taskScheduler.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    if (exportImportService.isImporting()) {
                        return;
                    }
                    mySqlFullTextSupportService.syncNewCards();
                    mySqlFullTextSupportService.syncUpdatedCards();
                    mySqlFullTextSupportService.syncNewCardData();
                    mySqlFullTextSupportService.syncUpdatedCardData();
                }
            }, 2 * 1000);
        }

        Integer timespan = NumberUtils.toInt(configurationRepository.getValueOrNull(Key.EMAIL_NOTIFICATION_TIMESPAN), 30);

        taskScheduler.scheduleAtFixedRate(new EmailNotificationHandler(configurationRepository, notificationService, exportImportService),
            timespan * 1000);
    }
}
