
package io.collapp.service;

import io.collapp.model.*;
import io.collapp.model.BoardColumn.BoardColumnLocation;
import io.collapp.query.BoardQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.trimToNull;

@Repository
@Transactional(readOnly = true)
public class BoardRepository {

	private final BoardColumnRepository boardColumnRepository;
	private final BoardQuery queries;

	public BoardRepository(BoardQuery queries, BoardColumnRepository boardColumnRepository) {
		this.boardColumnRepository = boardColumnRepository;
		this.queries = queries;
	}

	@Transactional(readOnly = false)
	public Board createEmptyBoard(String name, String shortName, String description, int projectId) {
		queries.createNewBoard(trimToNull(name), trimToNull(shortName.toUpperCase(Locale.ENGLISH)),
				trimToNull(description), projectId);
		queries.initializeSequence();

		return queries.findLastCreatedBoard();
	}

	/**
	 * Returns the new BOARD.
	 * <p/>
	 * Additionally, add some predefined SYSTEM labels.
	 *
	 * @param name
	 * @param shortName
	 * @param description
	 * @return
	 */
	@Transactional(readOnly = false)
	public Board createNewBoard(String name, String shortName, String description, int projectId) {
		Board board = createEmptyBoard(name, shortName, description, projectId);

		BoardColumnDefinition closedDefinition = findColumnDefinitionByProjectIdAndType(ColumnDefinition.CLOSED,
				projectId);
		BoardColumnDefinition backlogDefinition = findColumnDefinitionByProjectIdAndType(ColumnDefinition.BACKLOG,
				projectId);
		// Add the ARCHIVE and BACKLOG columns
		boardColumnRepository.addColumnToBoardPosition(BoardColumnLocation.ARCHIVE.toString(), closedDefinition.getId(),
				BoardColumnLocation.ARCHIVE, 0, board.getId());
		boardColumnRepository.addColumnToBoardPosition(BoardColumnLocation.BACKLOG.toString(), backlogDefinition.getId(),
				BoardColumnLocation.BACKLOG, 0, board.getId());
		boardColumnRepository.addColumnToBoardPosition(BoardColumnLocation.TRASH.toString(), closedDefinition.getId(),
				BoardColumnLocation.TRASH, 0, board.getId());
		return board;
	}

	@Transactional(readOnly = false)
	public Board updateBoard(int boardId, String name, String description, boolean archived) {
		queries.updateBoard(boardId, name, description, archived);
		return queries.findBoardById(boardId);
	}

	public Integer findBoardIdByShortName(String shortName) {
		return queries.findBoardIdByShortName(shortName);
	}

	public Board findBoardByShortName(String shortName) {
		return queries.findBoardByShortName(shortName);
	}

	public boolean existsWithShortName(String shortName) {
		return Integer.valueOf(1).equals(queries.existsWithShortName(shortName));
	}

	public Board findBoardById(int boardId) {
		return queries.findBoardById(boardId);
	}

	public List<Board> findAll() {
		return queries.findAll();
	}

	public List<BoardInfo> findBoardInfo(int projectId) {
		return queries.findBoardInfo(projectId);
	}

	public BoardColumnDefinition findColumnDefinitionByProjectIdAndType(ColumnDefinition definition, int projectId) {
		return queries.findColumnDefinitionByProjectIdAndType(projectId, definition.toString());
	}

	public ProjectAndBoard findProjectAndBoardByColumnId(int columnId) {
		return queries.findProjectAndBoardByColumnId(columnId);
	}
}
