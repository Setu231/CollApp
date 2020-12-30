
package io.collapp.service;

import io.collapp.config.PersistenceAndServiceConfig;
import io.collapp.model.*;
import io.collapp.service.BoardColumnRepository;
import io.collapp.service.BoardRepository;
import io.collapp.service.ProjectService;
import io.collapp.service.config.TestServiceConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class, PersistenceAndServiceConfig.class })
@Transactional
public class BoardRepositoryTest {

	private static final String TEST_BOARD = "TEST-BRD";

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private BoardColumnRepository boardColumnRepository;

	private Board board;

	private Project project;

	@Before
	public void createUserAndBoard() {
		projectService.create("test", "TEST", "desc");
		project = projectService.findByShortName("TEST");
		boardRepository.createNewBoard("TEST", TEST_BOARD, "TEST", project.getId());
		board = boardRepository.findBoardByShortName(TEST_BOARD);
	}

	@Test
	public void testRepoCreation() {
		Assert.assertEquals("TEST", boardRepository.createNewBoard("TEST", "TEST-BR2", "TEST", project.getId())
				.getName());
	}

	@Test
	public void updateBoardTest() {
		Board b = boardRepository.createNewBoard("TEST", "TEST-BR2", "TEST", project.getId());

		Board newBoard = boardRepository.updateBoard(b.getId(), "new name", b.getDescription(), b.getArchived());

		Assert.assertEquals(b.getId(), newBoard.getId());
		Assert.assertEquals(b.getDescription(), newBoard.getDescription());
		Assert.assertNotEquals(b.getName(), newBoard.getName());
		Assert.assertEquals("new name", newBoard.getName());
	}

	/**
	 * We must have a unique constraint on the short name
	 */
	@Test(expected = DuplicateKeyException.class)
	public void testRepoCreationConstraint() {
		Assert.assertEquals("TEST", boardRepository.createNewBoard("TEST", "TEST-BR2", "TEST", project.getId())
				.getName());

		boardRepository.createNewBoard("TEST", "TEST-BR2", "TEST", project.getId());
	}

	@Test
	public void findBoardInfoTest() {
		Assert.assertEquals(1, boardRepository.findBoardInfo(project.getId()).size());
	}

	@Test
	public void testFindByShortName() {
		Assert.assertNotNull(boardRepository.findBoardByShortName(TEST_BOARD));
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void testNothingFoundForFindByShortName() {
		boardRepository.findBoardByShortName("NO-BOARD");
	}

	@Test
	public void testFindIdByShortName() {
		Assert.assertEquals(board.getId(), boardRepository.findBoardIdByShortName(TEST_BOARD).intValue());
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void testNothingFoundForFindIdByShortName() {
		boardRepository.findBoardIdByShortName("NO-BOARD");
	}

	@Test
	public void testFindBoard() {
		Board f = boardRepository.findBoardById(board.getId());
		Assert.assertEquals(board.getId(), f.getId());
		Assert.assertEquals(board.getShortName(), f.getShortName());
		Assert.assertEquals(board.getName(), f.getName());
		Assert.assertEquals(board.getDescription(), f.getDescription());
	}

	@Test
	public void testFindProjectAndBoardByColumnId() {
		List<BoardColumnDefinition> definitions = projectService.findColumnDefinitionsByProjectId(project.getId());
		BoardColumn bc = boardColumnRepository.addColumnToBoard("col1", definitions.get(0).getId(),
				BoardColumn.BoardColumnLocation.BOARD, board.getId());

		ProjectAndBoard pab = boardRepository.findProjectAndBoardByColumnId(bc.getId());

		Assert.assertEquals(project.getId(), pab.getProject().getId());
		Assert.assertEquals(board.getId(), pab.getBoard().getId());
	}

}
