
package io.collapp.model

import ch.digitalfondue.npjt.ConstructorAnnotationRowMapper.Column
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder

open class CardLabel(@Column("CARD_LABEL_ID") val id: Int,
                     @Column("CARD_LABEL_PROJECT_ID_FK") val projectId: Int,
                     @Column("CARD_LABEL_UNIQUE") val unique: Boolean,
                     @Column("CARD_LABEL_TYPE") val type: CardLabel.LabelType,
                     @Column("CARD_LABEL_DOMAIN") val domain: CardLabel.LabelDomain,
                     @Column("CARD_LABEL_NAME") val name: String,
                     @Column("CARD_LABEL_COLOR") val color: Int) {

    fun name(newName: String): CardLabel {
        return CardLabel(id, projectId, unique, type, domain, newName, color)
    }

    fun color(newColor: Int): CardLabel {
        return CardLabel(id, projectId, unique, type, domain, name, newColor)
    }

    operator fun set(newName: String, newType: LabelType, newColor: Int): CardLabel {
        return CardLabel(id, projectId, unique, newType, domain, newName, newColor)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null || obj !is CardLabel) {
            return false
        }
        return EqualsBuilder().append(id, obj.id).append(projectId, obj.projectId).append(unique, obj.unique).append(type, obj.type).append(domain, obj.domain).append(name, obj.name).append(color, obj.color).isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(id).append(projectId).append(unique).append(type).append(domain).append(name).append(color).toHashCode()
    }

    enum class LabelDomain {
        SYSTEM, USER
    }

    enum class LabelType {
        NULL, STRING, TIMESTAMP, INT, CARD, USER, LIST
    }
}
