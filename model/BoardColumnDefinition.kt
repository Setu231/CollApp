
package io.collapp.model

import ch.digitalfondue.npjt.ConstructorAnnotationRowMapper.Column

class BoardColumnDefinition(@Column("BOARD_COLUMN_DEFINITION_ID") val id: Int,
                            @Column("BOARD_COLUMN_DEFINITION_PROJECT_ID_FK") val projectId: Int,
                            @Column("BOARD_COLUMN_DEFINITION_VALUE") val value: ColumnDefinition,
                            @Column("BOARD_COLUMN_DEFINITION_COLOR") val color: Int)
