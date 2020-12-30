
package io.collapp.web.api;

import io.collapp.model.Board;
import io.collapp.model.BoardColumnDefinition;
import io.collapp.model.ColumnDefinition;
import io.collapp.model.UserWithPermission;
import io.collapp.service.*;
import io.collapp.web.api.BoardController;
import io.collapp.web.api.model.Suggestion;
import io.collapp.web.api.model.UpdateRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BoardControllerTest {

	@Mock
	BoardRepository boardRepository;
	@Mock
	ProjectService projectService;
	@Mock
	SearchService searchService;
	@Mock
	EventEmitter eventEmitter;
	@Mock
	StatisticsService statisticsService;
	@Mock
	UserWithPermission user;

	private BoardController boardController;

	private final String shortName = "SHORT";

	@Before
	public void prepare() {
		boardController = new BoardController(boardRepository, projectService, searchService, eventEmitter,
				statisticsService);
	}

	@Test
	public void testFindByShortName() {
		boardController.findByShortName(shortName);
		verify(boardRepository).findBoardByShortName(shortName);
	}

	@Test
	public void testSuggestShortBoardName() {
		Suggestion suggestion = boardController.suggestBoardShortName("test");
		Assert.assertEquals("TEST", suggestion.getSuggestion());
	}

	@Test
	public void testFindBoardByShortName() {
		Board b = new Board(1, "NAME", "SHORT", "desc", 5, false);
		when(boardRepository.findBoardByShortName("TEST")).thenReturn(b);

		Map<ColumnDefinition, BoardColumnDefinition> defs = new EnumMap<>(ColumnDefinition.class);
		defs.put(ColumnDefinition.OPEN, new BoardColumnDefinition(1, b.getProjectId(), ColumnDefinition.OPEN, 0));
		defs.put(ColumnDefinition.CLOSED, new BoardColumnDefinition(2, b.getProjectId(), ColumnDefinition.CLOSED, 0));
		defs.put(ColumnDefinition.BACKLOG, new BoardColumnDefinition(3, b.getProjectId(), ColumnDefinition.BACKLOG, 0));
		defs.put(ColumnDefinition.DEFERRED,
				new BoardColumnDefinition(4, b.getProjectId(), ColumnDefinition.DEFERRED, 0));

		when(projectService.findMappedColumnDefinitionsByProjectId(b.getProjectId())).thenReturn(defs);

		Map<ColumnDefinition, Integer> tasks = new EnumMap<>(ColumnDefinition.class);
		tasks.put(ColumnDefinition.OPEN, 0);
		tasks.put(ColumnDefinition.CLOSED, 0);
		tasks.put(ColumnDefinition.BACKLOG, 0);
		tasks.put(ColumnDefinition.DEFERRED, 0);
		when(searchService
				.findTaksByColumnDefinition(eq(b.getProjectId()), eq(b.getId()), any(Boolean.class), eq(user)))
				.thenReturn(tasks);

		boardController.boardStatistics("TEST", new Date(), user);
		verify(boardRepository).findBoardByShortName(eq("TEST"));
	}

	@Test
	public void update() {
		Board b = mock(Board.class);
		when(boardRepository.findBoardByShortName(shortName)).thenReturn(b);

		UpdateRequest updatedBoard = new UpdateRequest();
		updatedBoard.setName("New name");
		updatedBoard.setDescription("Updated desc");

		boardController.updateBoard(shortName, updatedBoard, user);

		verify(boardRepository).updateBoard(eq(b.getId()), eq("New name"), eq("Updated desc"), eq(false));
	}
}
