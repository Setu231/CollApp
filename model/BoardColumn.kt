
package io.collapp.model

import ch.digitalfondue.npjt.ConstructorAnnotationRowMapper.Column
import io.collapp.model.Event.EventType
import java.util.*

open class BoardColumn(
        @Column("BOARD_COLUMN_ID") val id: Int, //
        @Column("BOARD_COLUMN_NAME") val name: String, //
        @Column("BOARD_COLUMN_ORDER") val order: Int, //
        @Column("BOARD_COLUMN_BOARD_ID_FK") val boardId: Int, //
        @Column("BOARD_COLUMN_LOCATION") val location: BoardColumn.BoardColumnLocation,
        @Column("BOARD_COLUMN_DEFINITION_ID") val definitionId: Int,
        @Column("BOARD_COLUMN_DEFINITION_VALUE") val status: ColumnDefinition,
        @Column("BOARD_COLUMN_DEFINITION_COLOR") val color: Int) {

    enum class BoardColumnLocation {

        BOARD, BACKLOG, ARCHIVE, TRASH;


        companion object {

            val MAPPING: Map<BoardColumnLocation, EventType>

            init {
                val mapping = EnumMap<BoardColumnLocation, EventType>(BoardColumnLocation::class.java)
                mapping.put(BoardColumnLocation.ARCHIVE, EventType.CARD_ARCHIVE)
                mapping.put(BoardColumnLocation.BACKLOG, EventType.CARD_BACKLOG)
                mapping.put(BoardColumnLocation.TRASH, EventType.CARD_TRASH)
                mapping.put(BoardColumnLocation.BOARD, EventType.CARD_CREATE)
                MAPPING = Collections.unmodifiableMap(mapping)
            }
        }
    }
}
