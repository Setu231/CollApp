
package io.collapp.service;

import io.collapp.model.BoardColumn;
import io.collapp.model.CardFull;
import io.collapp.model.CardLabel.LabelType;
import io.collapp.model.Event;
import io.collapp.model.User;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.String.format;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

/**
 * Contains all the necessary data for formatting an email to the user.
 */
class EventsContext {
	// aggregate the events by card id
	final Map<Integer, List<Event>> events = new TreeMap<>();
	final Map<Integer, User> users = new HashMap<>();
	final Map<Integer, CardFull> cards = new HashMap<>();
	final Map<Integer, String> cardData;
	final Map<Integer, BoardColumn> columns = new HashMap<>();

	EventsContext(List<Event> events, List<User> users, List<CardFull> cards, Map<Integer, String> cardData,
			List<BoardColumn> columns) {
		this.cardData = cardData;

		for (Event e : events) {
			if (!this.events.containsKey(e.getCardId())) {
				this.events.put(e.getCardId(), new ArrayList<Event>());
			}
			this.events.get(e.getCardId()).add(e);
		}
		for (User u : users) {
			this.users.put(u.getId(), u);
		}
		for (CardFull c : cards) {
			this.cards.put(c.getId(), c);
		}
		for (BoardColumn bc : columns) {
			this.columns.put(bc.getId(), bc);
		}
	}

	String formatLabel(Event e) {
		return new StringBuilder(e.getLabelName()).append(e.getLabelType() != LabelType.NULL ? "::" : "")
				.append(formatLabelValue(e)).toString();
	}

	String formatLabelValue(Event e) {
		if (e.getLabelType() == LabelType.STRING) {
			return e.getValueString();
		} else if (e.getLabelType() == LabelType.INT) {
			return Integer.toString(e.getValueInt());
		} else if (e.getLabelType() == LabelType.TIMESTAMP) {
			return new SimpleDateFormat("dd.MM.yyyy").format(e.getValueTimestamp());
		} else if (e.getLabelType() == LabelType.CARD) {
			CardFull cf = cards.get(e.getValueCard());
			return cf.getBoardShortName() + "-" + cf.getSequence();
		} else if (e.getLabelType() == LabelType.USER) {
			return formatUser(e.getValueUser());
		} else {
			return "";
		}
	}

	String formatColumn(Integer colId) {
		if (colId != null && columns.containsKey(colId)) {
			BoardColumn col = columns.get(colId);
			return format("%s (%s::%s)", col.getName(), col.getLocation(), col.getStatus());
		} else {
			return "-";
		}
	}

	String formatUser(int userId) {
		User u = users.get(userId);
		String name = firstNonNull(u.getDisplayName(), u.getEmail(), u.getProvider() + ":" + u.getUsername());
		return name + (u.getEmail() != null && !name.equals(u.getEmail()) ? " <" + u.getEmail() + ">" : "");
	}
}
