
package io.collapp.config;


import io.collapp.common.collappEnvironment;
import io.collapp.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
public class SchedulingServiceConfig {

	@Bean
	public Scheduler getScheduler(TaskScheduler taskScheduler, collappEnvironment env,
                                  ConfigurationRepository configurationRepository,
                                  MySqlFullTextSupportService mySqlFullTextSupportService,
                                  NotificationService notificationService,
                                  StatisticsService statisticsService,
                                  MailTicketService mailTicketService,
                                  ExportImportService exportImportService) {
		return new Scheduler(taskScheduler, env, configurationRepository,
				mySqlFullTextSupportService, notificationService,
				statisticsService, mailTicketService, exportImportService);
	}
}
