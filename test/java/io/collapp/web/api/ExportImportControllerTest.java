
package io.collapp.web.api;

import io.collapp.model.User;
import io.collapp.service.ExportImportService;
import io.collapp.service.ImportService;
import io.collapp.web.api.ExportImportController;
import io.collapp.web.api.model.TrelloImportRequest;
import io.collapp.web.api.model.TrelloImportRequest.BoardIdAndShortName;
import io.collapp.web.api.model.TrelloRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExportImportControllerTest {

	@Mock
	private ImportService importService;

	@Mock
	private ExportImportService exportImportService2;

	@Mock
	private User user;

	private ExportImportController importController;

	@Before
	public void prepare() {
		importController = new ExportImportController(exportImportService2, importService);
	}

	@Test
	public void getAvailableTrelloBoardsTest() {
		TrelloRequest request = new TrelloRequest();
		request.setApiKey("KEY");
		request.setSecret("SECRET");
		importController.getAvailableTrelloBoards(request);
		verify(importService).getAvailableTrelloBoards(request);
	}

	@Test
	public void importFromTrelloTest() {
		TrelloImportRequest importRequest = new TrelloImportRequest();
		importRequest.setApiKey("KEY");
		importRequest.setSecret("SECRET");
		importRequest.setImportId("ID");
		BoardIdAndShortName boardIdAndShortName = new BoardIdAndShortName();
		boardIdAndShortName.setId("BOARD_ID");
		boardIdAndShortName.setShortName("SHORT_NAME");
		importRequest.setBoards(Arrays.asList(boardIdAndShortName));
		importController.importFromTrello(importRequest, user);
		verify(importService).importFromTrello(importRequest, user);
	}

	@Test
	public void saveTrelloBoardsToDbTest() {
		TrelloImportRequest importRequest = new TrelloImportRequest();
		importRequest.setProjectShortName("Dummy");
		importRequest.setApiKey("KEY");
		importRequest.setSecret("SECRET");
		importController.importFromTrello(importRequest, user);
		verify(importService).saveTrelloBoardsToDb(eq(importRequest.getProjectShortName()),
				isNull(ImportService.TrelloImportResponse.class), eq(user));
	}
}
