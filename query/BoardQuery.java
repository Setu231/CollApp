
package io.collapp.query;

import ch.digitalfondue.npjt.*;
import io.collapp.model.Board;
import io.collapp.model.BoardColumnDefinition;
import io.collapp.model.BoardInfo;
import io.collapp.model.ProjectAndBoard;

import java.util.List;

@QueryRepository
public interface BoardQuery {

	@Query("INSERT INTO LA_BOARD(BOARD_NAME, BOARD_SHORT_NAME, BOARD_DESCRIPTION, BOARD_PROJECT_ID_FK)  VALUES(:name, :shortName, :description, :projectId)")
	int createNewBoard(@Bind("name") String name, @Bind("shortName") String shortName,
			@Bind("description") String description, @Bind("projectId") int projectId);

	@Query("UPDATE LA_BOARD SET BOARD_NAME = :name, BOARD_DESCRIPTION = :description, BOARD_ARCHIVED = :archived WHERE BOARD_ID = :boardId")
	int updateBoard(@Bind("boardId") int boardId, @Bind("name") String name, @Bind("description") String description,
			@Bind("archived") boolean archived);

	@Query("SELECT * FROM LA_BOARD WHERE BOARD_SHORT_NAME = :shortName")
	Board findBoardByShortName(@Bind("shortName") String shortName);

	@Query("SELECT * FROM LA_BOARD WHERE BOARD_ID = :boardId")
	Board findBoardById(@Bind("boardId") int boardId);

	@Query("SELECT BOARD_ID FROM LA_BOARD WHERE BOARD_SHORT_NAME = :shortName")
	Integer findBoardIdByShortName(@Bind("shortName") String shortName);

	@Query("SELECT * FROM LA_BOARD WHERE BOARD_ID = IDENTITY()")
	@QueriesOverride({
			@QueryOverride(db = DB.MYSQL, value = "SELECT * FROM LA_BOARD WHERE BOARD_ID = LAST_INSERT_ID()"),//
			@QueryOverride(db = DB.PGSQL, value = "SELECT * FROM LA_BOARD WHERE BOARD_ID = (SELECT CURRVAL(pg_get_serial_sequence('la_board','board_id')))"), })
	Board findLastCreatedBoard();

	@Query("INSERT INTO LA_BOARD_COUNTER(BOARD_COUNTER_ID_FK, BOARD_COUNTER_CARD_SEQUENCE) VALUES(IDENTITY() , 1)")
	@QueriesOverride({
			@QueryOverride(db = DB.MYSQL, value = "INSERT INTO LA_BOARD_COUNTER(BOARD_COUNTER_ID_FK, BOARD_COUNTER_CARD_SEQUENCE) VALUES (LAST_INSERT_ID(), 1)"),//
			@QueryOverride(db = DB.PGSQL, value = "INSERT INTO LA_BOARD_COUNTER(BOARD_COUNTER_ID_FK, BOARD_COUNTER_CARD_SEQUENCE) VALUES ((SELECT CURRVAL(pg_get_serial_sequence('la_board','board_id'))), 1)"), })
	int initializeSequence();

	@Query("SELECT * FROM LA_BOARD ORDER BY BOARD_SHORT_NAME")
	List<Board> findAll();

	@Query("SELECT * FROM LA_BOARD_COLUMN_DEFINITION where BOARD_COLUMN_DEFINITION_VALUE = :definition and BOARD_COLUMN_DEFINITION_PROJECT_ID_FK = :projectId")
	BoardColumnDefinition findColumnDefinitionByProjectIdAndType(@Bind("projectId") int projectId,
			@Bind("definition") String definition);

	@Query("SELECT BOARD_SHORT_NAME, BOARD_NAME, BOARD_DESCRIPTION, BOARD_ARCHIVED FROM LA_BOARD WHERE BOARD_PROJECT_ID_FK = :projectId ORDER BY BOARD_SHORT_NAME")
	List<BoardInfo> findBoardInfo(@Bind("projectId") int projectId);

	@Query("SELECT LA_PROJECT.PROJECT_ID, LA_PROJECT.PROJECT_NAME, LA_PROJECT.PROJECT_SHORT_NAME, PROJECT_DESCRIPTION, PROJECT_ARCHIVED, "
			+ "BOARD_ID, LA_BOARD.BOARD_SHORT_NAME, BOARD_NAME, BOARD_DESCRIPTION, BOARD_ARCHIVED FROM LA_PROJECT "
			+ "INNER JOIN LA_BOARD ON LA_PROJECT.PROJECT_ID = LA_BOARD.BOARD_PROJECT_ID_FK "
			+ "INNER JOIN LA_BOARD_COLUMN ON LA_BOARD.BOARD_ID = LA_BOARD_COLUMN.BOARD_COLUMN_BOARD_ID_FK "
			+ "WHERE LA_BOARD_COLUMN.BOARD_COLUMN_ID = :columnId")
	ProjectAndBoard findProjectAndBoardByColumnId(@Bind("columnId") int columnId);

	@Query("SELECT COUNT(BOARD_SHORT_NAME) FROM LA_BOARD WHERE BOARD_SHORT_NAME = :shortName")
	Integer existsWithShortName(@Bind("shortName") String shortName);
}
