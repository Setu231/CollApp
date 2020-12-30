
package io.collapp.service;

import io.collapp.config.PersistenceAndServiceConfig;
import io.collapp.model.*;
import io.collapp.service.BoardRepository;
import io.collapp.service.CardDataService;
import io.collapp.service.CardLabelRepository;
import io.collapp.service.CardRepository;
import io.collapp.service.ImportService;
import io.collapp.service.ProjectService;
import io.collapp.service.UserRepository;
import io.collapp.service.UserService;
import io.collapp.service.config.TestServiceConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class, PersistenceAndServiceConfig.class })
@Transactional
public class ImportServiceTest {

	@Autowired
	private ImportService importService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private CardLabelRepository cardLabelRepository;

	@Autowired
	private CardDataService cardDataService;

	private Project project;
	private User user;

	@Before
	public void prepare() {
		project = projectService.create("test", "TEST", "desc");
		userService.createUser(new UserToCreate("test", "test"));
		user = userRepository.findUserByName("test", "test");
	}

	private ImportService.TrelloImportResponse getBaseImport() {
		ImportService.TrelloImportResponse tImport = new ImportService.TrelloImportResponse();
		ImportService.TrelloBoard board = new ImportService.TrelloBoard("T BOARD", "T BOARD", "T Desc");
		tImport.getBoards().add(board);
		board.getColumns().put(0, new ImportService.TrelloBoardColumn("T Column"));

		return tImport;
	}

	private Board getCreatedBoard() {
		List<Board> boardInfos = boardRepository.findAll();
		Assert.assertEquals(1, boardInfos.size());
		return boardInfos.get(0);
	}

	@Test
	public void saveTrelloBoardsToDbTest() {
		ImportService.TrelloImportResponse tImport = getBaseImport();
		ImportService.TrelloBoardColumn column = tImport.getBoards().get(0).getColumns().get(0);

		// Cards
		ImportService.TrelloCard card1 = new ImportService.TrelloCard("Card1", "Desc", false, null);
		card1.getAssignedUsers().add(user);
		column.getCards().put(0, card1);

		ImportService.TrelloCard card2 = new ImportService.TrelloCard("Card2", "Desc", false, null);
		column.getCards().put(1, card2);

		ImportService.TrelloCard card3 = new ImportService.TrelloCard("Archived Card3", "Desc", true, new Date());
		card3.getComments().add(new ImportService.TrelloComment(new Date(), user, "Comment"));
		column.getCards().put(2, card3);

		// Run the import
		importService.saveTrelloBoardsToDb("TEST", tImport, user);

		// Asserts
		Board createdBoard = getCreatedBoard();
		Assert.assertEquals("T BOARD", createdBoard.getName());
		Assert.assertEquals(project.getId(), createdBoard.getProjectId());

		List<Card> cards = cardRepository
				.findAllByBoardIdAndLocation(createdBoard.getId(), BoardColumn.BoardColumnLocation.BOARD);
		Assert.assertEquals(2, cards.size());
		for (Card card : cards) {
			Assert.assertEquals(0, cardDataService.findAllCommentsByCardId(card.getId()).size());
		}
		List<Card> archivedCards = cardRepository.findAllByBoardIdAndLocation(createdBoard.getId(),
				BoardColumn.BoardColumnLocation.ARCHIVE);
		Assert.assertEquals(1, archivedCards.size());
		Assert.assertEquals(1, cardDataService.findAllCommentsByCardId(archivedCards.get(0).getId()).size());
	}

	@Test
	public void saveTrelloBoardsToDbLabelsTest() {
		ImportService.TrelloImportResponse tImport = getBaseImport();
		ImportService.TrelloBoardColumn column = tImport.getBoards().get(0).getColumns().get(0);

		// Labels
		tImport.getLabels().put("Orange Label", "orange");
		tImport.getLabels().put("Yellow Label", "yellow");
		tImport.getLabels().put("Lime Label", "lime");
		tImport.getLabels().put("Sky Label", "sky");
		tImport.getLabels().put("Red Label", "red");
		tImport.getLabels().put("Blue Label", "blue");
		tImport.getLabels().put("Black Label", "black");
		tImport.getLabels().put("Green Label", "green");
		tImport.getLabels().put("Pink Label", "pink");
		tImport.getLabels().put("Purple Label", "purple");
		tImport.getLabels().put("Bad Label", "---");

		// Card
		ImportService.TrelloCard card1 = new ImportService.TrelloCard("Card1", "Desc", false, null);
		card1.getLabels().add("Black Label");
		column.getCards().put(0, card1);

		// Run the import
		importService.saveTrelloBoardsToDb("TEST", tImport, user);

		// Asserts
		Board createdBoard = getCreatedBoard();
		List<Card> cards = cardRepository.findAllByBoardIdAndLocation(createdBoard.getId(),
				BoardColumn.BoardColumnLocation.BOARD);
		Assert.assertEquals(1, cards.size());

		Map<CardLabel, List<CardLabelValue>> values = cardLabelRepository
				.findCardLabelValuesByCardId(cards.get(0).getId());
		Assert.assertEquals(1, values.size());
	}

	@Test
	public void saveTrelloBoardsToDbCheckListTest() {
		ImportService.TrelloImportResponse tImport = getBaseImport();
		ImportService.TrelloBoardColumn column = tImport.getBoards().get(0).getColumns().get(0);

		ImportService.TrelloCard card = new ImportService.TrelloCard("Checklist Card", "Desc", false, new Date());
		ImportService.TrelloChecklist checklist = new ImportService.TrelloChecklist("TODO");
		checklist.getItems().put(0, new ImportService.TrelloChecklistItem("Task 1", true));
		checklist.getItems().put(1, new ImportService.TrelloChecklistItem("Task 2", false));
		card.getChecklists().add(checklist);
		column.getCards().put(3, card);

		// Run the import
		importService.saveTrelloBoardsToDb("TEST", tImport, user);

		// Asserts
		Board createdBoard = getCreatedBoard();
		List<Card> cards = cardRepository.findAllByBoardIdAndLocation(createdBoard.getId(),
				BoardColumn.BoardColumnLocation.BOARD);
		Assert.assertEquals(1, cards.size());

		List<CardData> data = cardDataService.findAllActionListsAndItemsByCardId(cards.get(0).getId());
		Assert.assertEquals(3, data.size());
		Assert.assertEquals("TODO", data.get(0).getContent());
	}
}
