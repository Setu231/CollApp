
package io.collapp.service;

import io.collapp.config.PersistenceAndServiceConfig;
import io.collapp.model.*;
import io.collapp.service.BoardColumnRepository;
import io.collapp.service.BoardRepository;
import io.collapp.service.MailTicketService;
import io.collapp.service.ProjectService;
import io.collapp.service.config.TestServiceConfig;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class, PersistenceAndServiceConfig.class })
@Transactional
public class MailTicketServiceTest {

    @Autowired
    private MailTicketService mailTicketService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BoardColumnRepository boardColumnRepository;

    @Autowired
    private ProjectService projectService;

    private Project project;
    private Board board;
    private BoardColumn col;
    private BoardColumn col2;

    private ProjectMailTicketConfigData data = new ProjectMailTicketConfigData("pop3",
        "inboundserver",
        1,
        "user",
        "password",
        null,
        "",
        "outboundServer",
        2,
        "smtp",
        "noreply@test.com",
        "user",
        "password",
        ""
        );

    @Before
    public void prepare() {
        project = projectService.create("UNITTEST", "UNITTEST", null);
        board = boardRepository.createNewBoard("test", "TEST", null, project.getId());
        List<BoardColumnDefinition> colDef = projectService.findColumnDefinitionsByProjectId(project.getId());

        BoardColumnDefinition openCol = null;
        for (BoardColumnDefinition bcd : colDef) {
            if (bcd.getValue() == ColumnDefinition.OPEN) {
                openCol = bcd;
            }
        }

        Assert.assertNotNull(openCol);

        col = boardColumnRepository
            .addColumnToBoard("col1", openCol.getId(), BoardColumn.BoardColumnLocation.BOARD, board.getId());
        col2 = boardColumnRepository
            .addColumnToBoard("col2", openCol.getId(), BoardColumn.BoardColumnLocation.BOARD, board.getId());
    }

    @Test
    public void TestCreateMailConfig() {
        ProjectMailTicketConfig config = getAndAssertConfig();

        Assert.assertEquals(config.getName(), "config");
        Assert.assertEquals(config.getProjectId(), project.getId());

        ProjectMailTicketConfigData mailTicketConfigData = config.getConfig();

        Assert.assertTrue(EqualsBuilder.reflectionEquals(data, mailTicketConfigData));
    }

    @Test
    public void TestDisabledMailConfig() {
        ProjectMailTicketConfig config = getAndAssertConfig();

        mailTicketService.updateConfig(config.getId(), config.getName(), !config.getEnabled(), config.getConfig(), config.getSubject(), config.getBody(), project.getId());

        ProjectMailTicketConfig disabledConfig = mailTicketService.findConfig(config.getId());

        Assert.assertFalse(disabledConfig.getEnabled());
        Assert.assertEquals(config.getId(), disabledConfig.getId());
    }

    @Test
    public void TestEditMailConfig() {
        ProjectMailTicketConfig config = getAndAssertConfig();

        mailTicketService.updateConfig(config.getId(), config.getName() + "updated", config.getEnabled(), config.getConfig(), config.getSubject(), config.getBody(), project.getId());

        ProjectMailTicketConfig updatedConfig = mailTicketService.findConfig(config.getId());

        Assert.assertEquals(config.getId(), updatedConfig.getId());
        Assert.assertEquals(config.getName() + "updated", updatedConfig.getName());
    }

    @Test
    public void TestCreateTicketConfig() {
        ProjectMailTicket ticketConfig = getAndAssertTicketConfig();

        Assert.assertEquals("ticket", ticketConfig.getName());
        Assert.assertEquals("alias@example.com", ticketConfig.getAlias());
        Assert.assertTrue(ticketConfig.getEnabled());
        Assert.assertFalse(ticketConfig.getSendByAlias());
        Assert.assertEquals(col.getId(), ticketConfig.getColumnId());
    }

    @Test
    public void TestDisableTicketConfig() {
        ProjectMailTicket ticketConfig = getAndAssertTicketConfig();

        mailTicketService.updateTicket(ticketConfig.getId(),
            ticketConfig.getName(),
            !ticketConfig.getEnabled(),
            ticketConfig.getAlias(),
            ticketConfig.getSendByAlias(),
            ticketConfig.getNotificationOverride(),
            ticketConfig.getSubject(),
            ticketConfig.getBody(),
            ticketConfig.getColumnId(),
            ticketConfig.getConfigId(),
            ticketConfig.getMetadata());

        ProjectMailTicket disabledTicketConfig = mailTicketService.findTicket(ticketConfig.getId());

        Assert.assertEquals(ticketConfig.getName(), disabledTicketConfig.getName());
        Assert.assertEquals(ticketConfig.getAlias(), disabledTicketConfig.getAlias());
        Assert.assertFalse(disabledTicketConfig.getEnabled());
        Assert.assertFalse(ticketConfig.getSendByAlias());
        Assert.assertEquals(ticketConfig.getColumnId(), disabledTicketConfig.getColumnId());
    }

    @Test
    public void TestEditTicketConfig() {
        ProjectMailTicket ticketConfig = getAndAssertTicketConfig();

        mailTicketService.updateTicket(ticketConfig.getId(),
            ticketConfig.getName() + "updated",
            ticketConfig.getEnabled(),
            "updated-" + ticketConfig.getAlias(),
            ticketConfig.getSendByAlias(),
            !ticketConfig.getNotificationOverride(),
            "updated-" + ticketConfig.getSubject(),
            "updated-" + ticketConfig.getBody(),
            col2.getId(),
            ticketConfig.getConfigId(),
            ticketConfig.getMetadata());

        ProjectMailTicket updatedTicketConfig = mailTicketService.findTicket(ticketConfig.getId());

        Assert.assertEquals(ticketConfig.getName() + "updated", updatedTicketConfig.getName());
        Assert.assertEquals("updated-" + ticketConfig.getAlias(), updatedTicketConfig.getAlias());
        Assert.assertEquals("updated-" + ticketConfig.getSubject(), updatedTicketConfig.getSubject());
        Assert.assertEquals("updated-" + ticketConfig.getBody(), updatedTicketConfig.getBody());
        Assert.assertTrue(updatedTicketConfig.getNotificationOverride() == !ticketConfig.getNotificationOverride());
        Assert.assertTrue(updatedTicketConfig.getEnabled());
        Assert.assertFalse(ticketConfig.getSendByAlias());
        Assert.assertEquals(col2.getId(), updatedTicketConfig.getColumnId());
    }

    @Test
    public void TestDeleteTicketConfig() {
        ProjectMailTicket ticketConfig = getAndAssertTicketConfig();

        int deletedRows = mailTicketService.deleteTicket(ticketConfig.getId());

        Assert.assertEquals(1, deletedRows);

        ProjectMailTicketConfig config = mailTicketService.findConfig(ticketConfig.getConfigId());

        Assert.assertEquals(0, config.getEntries().size());
    }

    @Test
    public void TestDeleteMailConfig() {
        ProjectMailTicketConfig config = getAndAssertConfig();

        int deletedRows = mailTicketService.deleteConfig(config.getId(), project.getId());

        Assert.assertEquals(1, deletedRows);

        Assert.assertTrue(mailTicketService.findAllByProject(project.getId()).size() == 0);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void TestDeleteMailConfigWithTicketConfigs() {
        ProjectMailTicket ticketConfig = getAndAssertTicketConfig();

        mailTicketService.deleteConfig(ticketConfig.getConfigId(), project.getId());
    }

    private ProjectMailTicketConfig getAndAssertConfig() {
        Assert.assertTrue(mailTicketService.findAllByProject(project.getId()).size() == 0);

        mailTicketService.addConfig("config",
            project.getId(),
            data,
            "subject",
            "body");

        List<ProjectMailTicketConfig> configs = mailTicketService.findAllByProject(project.getId());

        Assert.assertTrue(configs.size() == 1);

        return configs.get(0);
    }

    private ProjectMailTicket getAndAssertTicketConfig() {
        mailTicketService.addTicket("ticket",
            "alias@example.com",
            false,
            false,
            "subject",
            "body",
            col.getId(), getAndAssertConfig().getId(), "{}");

        List<ProjectMailTicketConfig> configs = mailTicketService.findAllByProject(project.getId());

        Assert.assertTrue(configs.size() == 1);

        ProjectMailTicketConfig config = configs.get(0);

        Assert.assertTrue(config.getEntries().size() == 1);

        ProjectMailTicket ticketConfig = config.getEntries().get(0);

        Assert.assertEquals(config.getId(), ticketConfig.getConfigId());

        return ticketConfig;
    }
}
