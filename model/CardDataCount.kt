
package io.collapp.model

import ch.digitalfondue.npjt.ConstructorAnnotationRowMapper.Column

class CardDataCount(@Column("CARD_ID") val cardId: Int, //
                    @Column("CARD_DATA_TYPE") val type: String, //
                    @Column("CARD_DATA_TYPE_COUNT") val count: Number)
