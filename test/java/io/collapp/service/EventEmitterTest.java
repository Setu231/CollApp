
package io.collapp.service;

import io.collapp.config.PersistenceAndServiceConfig;
import io.collapp.model.*;
import io.collapp.service.ApiHooksService;
import io.collapp.service.BoardColumnRepository;
import io.collapp.service.BoardRepository;
import io.collapp.service.CardRepository;
import io.collapp.service.CardService;
import io.collapp.service.EventEmitter;
import io.collapp.service.ProjectService;
import io.collapp.service.UserRepository;
import io.collapp.service.config.TestServiceConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class, PersistenceAndServiceConfig.class })
@Transactional
public class EventEmitterTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private BoardColumnRepository boardColumnRepository;

	@Autowired
	private CardService cardService;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private ProjectService projectService;

	@Mock
	private SimpMessageSendingOperations simpMessageSendingOperations;

	@Mock
	private ApiHooksService apiHooksService;

	private EventEmitter eventEmitter;
	private ArgumentCaptor<EventEmitter.Event> argument;
	private User user;
	private BoardColumn col1;

	@Before
	public void prepare() {
		MockitoAnnotations.initMocks(this);

		eventEmitter = new EventEmitter(simpMessageSendingOperations, apiHooksService);
		argument = ArgumentCaptor.forClass(EventEmitter.Event.class);

		Helper.createUser(userRepository, "test", "test-user");
		user = userRepository.findUserByName("test", "test-user");
		projectService.create("test", "TEST", "desc");
		boardRepository.createNewBoard("test-board", "TEST-BRD", null,
				projectService.findByShortName("TEST").getId());
		Board board = boardRepository.findBoardByShortName("TEST-BRD");
		List<BoardColumnDefinition> definitions = projectService.findColumnDefinitionsByProjectId(projectService
				.findByShortName("TEST").getId());
		boardColumnRepository.addColumnToBoard("col1", definitions.get(0).getId(),
				BoardColumn.BoardColumnLocation.BOARD, board.getId());
		List<BoardColumn> cols = boardColumnRepository.findAllColumnsFor(board.getId(),
				BoardColumn.BoardColumnLocation.BOARD);
		col1 = cols.get(0);
	}

	@Test
	public void emitCreateProjectTest() {
		eventEmitter.emitCreateProject("TEST", user);

		verify(simpMessageSendingOperations).convertAndSend(eq("/event/project"), argument.capture());
		assertEquals("TEST", argument.getValue().getPayload());
	}

	@Test
	public void emitUpdateProjectTest() {
		eventEmitter.emitUpdateProject("TEST", user);

		verify(simpMessageSendingOperations).convertAndSend(eq("/event/project"), argument.capture());
		assertEquals("TEST", argument.getValue().getPayload());
	}

	@Test
	public void emitUpdateUserProfileTest() {
		eventEmitter.emitUpdateUserProfile(5);

		verify(simpMessageSendingOperations).convertAndSend(eq("/event/user"), argument.capture());
		assertEquals(5, argument.getValue().getPayload());
	}

	private void verifyLabelEvents(CardFull cardFull) {
		verify(simpMessageSendingOperations)
				.convertAndSend(eq("/event/card/" + cardFull.getId() + "/card-data"), any(EventEmitter.Event.class));
		verify(simpMessageSendingOperations)
				.convertAndSend(eq("/event/column/" + cardFull.getColumnId() + "/card"), any(EventEmitter.Event.class));
		verify(simpMessageSendingOperations)
				.convertAndSend(eq("/event/project/" + cardFull.getProjectShortName() + "/label-value"),
						any(EventEmitter.Event.class));
	}

	@Test
	public void emitAddLabelValueToCardsTest() {
		Card card = cardService.createCard("card1", col1.getId(), new Date(), user);
		CardFull cardFull = cardRepository.findFullBy(card.getId());

		eventEmitter.emitAddLabelValueToCards(Arrays.asList(cardFull), 0, null, user);

		verifyLabelEvents(cardFull);
	}

	@Test
	public void emitRemoveLabelValueToCardsTest() {
		Card card = cardService.createCard("card1", col1.getId(), new Date(), user);
		CardFull cardFull = cardRepository.findFullBy(card.getId());

		eventEmitter.emitRemoveLabelValueToCards(Arrays.asList(cardFull), 0, null, user);

		verifyLabelEvents(cardFull);
	}

	@Test
	public void emitUpdateOrAddValueToCardsTest() {
		Card card = cardService.createCard("card1", col1.getId(), new Date(), user);
		CardFull cardFull = cardRepository.findFullBy(card.getId());

		eventEmitter.emitUpdateOrAddValueToCards(Arrays.asList(cardFull), new ArrayList<CardFull>(), 0, null, user);

		verifyLabelEvents(cardFull);
	}

	@Test
	public void emitUpdateBoardTest() {
		eventEmitter.emitUpdateBoard("TEST", user);

		verify(simpMessageSendingOperations).convertAndSend(eq("/event/board/TEST"), argument.capture());
		assertEquals(null, argument.getValue().getPayload());
	}
}
