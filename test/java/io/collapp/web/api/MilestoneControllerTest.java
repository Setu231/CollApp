
package io.collapp.web.api;

import io.collapp.model.*;
import io.collapp.service.*;
import io.collapp.web.api.MilestoneController;
import io.collapp.web.api.model.MilestoneInfo;
import io.collapp.web.api.model.Milestones;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.collapp.common.Constants.SYSTEM_LABEL_MILESTONE;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MilestoneControllerTest {

    @Mock
    private BoardColumnRepository boardColumnRepository;
    @Mock
    private CardLabelRepository cardLabelRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private StatisticsService statisticsService;
    @Mock
    private SearchService searchService;
    @Mock
    private ExcelExportService excelExportService;
    @Mock
    private Card card;
    @Mock
    private BoardColumn boardColumn;
    @Mock
    private Project project;
    @Mock
    private Board board;

    private MilestoneController milestoneController;
    @Mock
    private UserWithPermission user;

    @Before
    public void prepare() {
        milestoneController = new MilestoneController(cardLabelRepository, projectService,
            statisticsService, searchService, excelExportService);
    }

    @Test
    public void testFindCardsByMilestone() {
        when(cardLabelRepository.findLabelByName(1, SYSTEM_LABEL_MILESTONE, CardLabel.LabelDomain.SYSTEM)).thenReturn(
            new CardLabel(1, 1, true, CardLabel.LabelType.STRING, CardLabel.LabelDomain.SYSTEM, SYSTEM_LABEL_MILESTONE, 0));
        when(projectService.findByShortName("TEST")).thenReturn(new Project(1, "test", "TEST", "test project", false));

        List<MilestoneCount> counts = new ArrayList<>();
        MilestoneCount count = new MilestoneCount(null, ColumnDefinition.OPEN, 1);
        counts.add(count);
        when(statisticsService.findCardsCountByMilestone(1)).thenReturn(counts);

        List<LabelListValueWithMetadata> listValues = new ArrayList<>();
        when(cardLabelRepository.findListValuesByLabelId(1)).thenReturn(listValues);

        Milestones cardsByMilestone = milestoneController.findCardsByMilestone("TEST");

        Assert.assertEquals(1, cardsByMilestone.getMilestones().size());
        MilestoneInfo md = cardsByMilestone.getMilestones().get(0);
        Assert.assertEquals("Unassigned", md.getLabelListValue().getValue());
        Assert.assertEquals(0, cardsByMilestone.getStatusColors().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindCardsByMilestoneDetailWrongValue() {
        milestoneController.findCardsByMilestoneDetail("TEST", 1, user);
    }

    @Test
    public void testFindCardsByMilestoneDetail() {

        when(projectService.findIdByShortName("TEST")).thenReturn(1);

        LabelListValue llv = new LabelListValue(1, 6, 0, "1.0");
        LabelListValueWithMetadata llvm = new LabelListValueWithMetadata(llv, new HashMap<String, String>());
        when(cardLabelRepository.findListValueById(1)).thenReturn(llvm);


        milestoneController.findCardsByMilestoneDetail("TEST", 1, user);

        verify(cardLabelRepository).findListValueById(eq(1));

    }

    @Test
    public void testExportMilestoneToExcel() throws IOException {

        MockHttpServletResponse mockResp = new MockHttpServletResponse();

        when(excelExportService.exportMilestoneToExcel("TEST", "1.0", user)).thenReturn(new HSSFWorkbook());

        milestoneController.exportMilestoneToExcel("TEST", "1.0", user, mockResp);

        verify(excelExportService).exportMilestoneToExcel(eq("TEST"), eq("1.0"), eq(user));
    }
}
