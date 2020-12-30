

package io.collapp.service.calendarutils;

import io.collapp.model.*;

import java.util.Date;
import java.util.HashSet;

public class StandardCalendarEventHandler implements CalendarEventHandler {

    private final CalendarEvents events;

    public StandardCalendarEventHandler(CalendarEvents events) {
        this.events = events;
    }

    private CalendarEvents.MilestoneDayEvents getDayEventsFromDate(Date date) {
        if (!events.getDailyEvents().containsKey(date)) {
            events.getDailyEvents().put(date, new CalendarEvents.MilestoneDayEvents(
                new HashSet<CalendarEvents.MilestoneEvent>(),
                new HashSet<CardFullWithCounts>()));
        }
        return events.getDailyEvents().get(date);
    }

    public void addMilestoneEvent(String projectShortName, Date date, LabelListValueWithMetadata m,
        SearchResults cards) {

        double closed = 0;
        double total = 0;
        for (CardFullWithCounts card : cards.getFound()) {
            if (card.getColumnDefinition() == ColumnDefinition.CLOSED) {
                closed++;
            }
            total++;
        }

        final String name = String.format("%s (%.0f%%)", m.getValue(), total > 0 ? 100 * closed / total : 100);

        getDayEventsFromDate(date).getMilestones().add(new CalendarEvents.MilestoneEvent(projectShortName, name, m));

    }

    public void addCardEvent(CardFullWithCounts card, LabelAndValue lav) {

        Date date = lav.getLabelValueTimestamp();
        getDayEventsFromDate(date).getCards().add(card);

    }
}
