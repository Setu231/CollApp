
package io.collapp.service.importexport;

import io.collapp.model.*;
import io.collapp.model.Event.EventType;
import io.collapp.service.*;

import java.nio.file.Path;
import java.util.Date;

class LabelDelete extends AbstractProcessLabelEvent {

	LabelDelete(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService,
			LabelService labelService, CardLabelRepository cardLabelRepository, BoardRepository boardRepository,
			EventRepository eventRepository) {
		super(cardRepository, userRepository, cardDataService, labelService, cardLabelRepository, boardRepository,
				eventRepository);
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		CardLabelValue clv = findCardLabelValueBy(e);
		if (clv != null) {
			labelService.removeLabelValue(clv, user, time);
		} else {
			insertLabelEvent(e, event, time, EventType.LABEL_DELETE);
		}
	}
}
