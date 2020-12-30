
package io.collapp.service.calendarutils;

import io.collapp.model.CardFullWithCounts;
import io.collapp.model.LabelAndValue;
import io.collapp.model.LabelListValueWithMetadata;
import io.collapp.model.SearchResults;

import java.net.URISyntaxException;
import java.util.Date;

public interface CalendarEventHandler {


    void addCardEvent(CardFullWithCounts card, LabelAndValue lav) throws URISyntaxException;

    void addMilestoneEvent(String shortName, Date date, LabelListValueWithMetadata m, SearchResults cards)
        throws URISyntaxException;
}
