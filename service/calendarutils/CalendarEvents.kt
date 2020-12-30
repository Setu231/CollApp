
package io.collapp.service.calendarutils

import io.collapp.model.CardFullWithCounts
import io.collapp.model.LabelListValueWithMetadata
import java.util.*

class CalendarEvents
@java.beans.ConstructorProperties("dailyEvents") constructor(
    val dailyEvents: Map<Date, CalendarEvents.MilestoneDayEvents>) {

    class MilestoneDayEvents
    @java.beans.ConstructorProperties("milestones", "cards") constructor(
        val milestones: Set<MilestoneEvent>, val cards: Set<CardFullWithCounts>)

    class MilestoneEvent
    @java.beans.ConstructorProperties("projectShortName", "name", "label") constructor(
        projectShortName: String, name: String, label: LabelListValueWithMetadata) {
        var projectShortName: String
            internal set
        var name: String
            internal set
        var label: LabelListValueWithMetadata
            internal set

        init {
            this.projectShortName = projectShortName
            this.name = name
            this.label = label
        }
    }
}
