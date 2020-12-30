
package io.collapp.service;

import io.collapp.config.PersistenceAndServiceConfig;
import io.collapp.model.*;
import io.collapp.service.BoardColumnRepository;
import io.collapp.service.BoardRepository;
import io.collapp.service.CardDataRepository;
import io.collapp.service.CardRepository;
import io.collapp.service.CardService;
import io.collapp.service.ProjectService;
import io.collapp.service.UserRepository;
import io.collapp.service.config.TestServiceConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class, PersistenceAndServiceConfig.class })
@Transactional
public class CardDataRepositoryTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private BoardColumnRepository boardColumnRepository;

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private CardService cardService;

	@Autowired
	private CardDataRepository cardDataRepo;

	private Card card1;

	@Before
	public void prepare() {
		Helper.createUser(userRepository, "test", "test-user");
		User user = userRepository.findUserByName("test", "test-user");

		Project project = projectService.create("test", "TEST", "desc");
		Board board = boardRepository.createNewBoard("test-board", "TEST-BRD", null, project.getId());

		List<BoardColumnDefinition> definitions = projectService.findColumnDefinitionsByProjectId(project.getId());
		boardColumnRepository.addColumnToBoard("col1", definitions.get(0).getId(),
				BoardColumn.BoardColumnLocation.BOARD,
				board.getId());
		List<BoardColumn> cols = boardColumnRepository
				.findAllColumnsFor(board.getId(), BoardColumn.BoardColumnLocation.BOARD);
		BoardColumn col1 = cols.get(0);
		cardService.createCard("card1", col1.getId(), new Date(), user);
		List<CardFull> cards = cardRepository.findAllByColumnId(col1.getId());
		card1 = cards.get(0);
	}

	@Test
	public void testFindMetadataById() {
		cardDataRepo.createData(card1.getId(), CardType.COMMENT, "TEST");
		List<CardData> data = cardDataRepo.findAllDataLightByCardId(card1.getId());
		Assert.assertEquals(1, data.size());

		CardDataMetadata cdm = cardDataRepo.findMetadataById(data.get(0).getId());

		Assert.assertEquals(card1.getId(), cdm.getCardId());
		Assert.assertEquals(CardType.COMMENT, cdm.getType());
	}

	@Test
	public void testFindDataByIdsWithEmptyCollection() {
		Assert.assertEquals(0, cardDataRepo.findDataByIds(new ArrayList<Integer>()).size());
	}
}
