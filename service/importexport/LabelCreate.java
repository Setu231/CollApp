
package io.collapp.service.importexport;

import io.collapp.model.*;
import io.collapp.model.CardLabelValue.LabelValue;
import io.collapp.model.Event.EventType;
import io.collapp.service.*;

import java.nio.file.Path;
import java.util.Date;

class LabelCreate extends AbstractProcessLabelEvent {

	LabelCreate(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService,
			LabelService labelService, CardLabelRepository cardLabelRepository, BoardRepository boardRepository,
			EventRepository eventRepository) {
		super(cardRepository, userRepository, cardDataService, labelService, cardLabelRepository, boardRepository,
				eventRepository);
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		CardLabel cl = findLabelByEvent(e);
		LabelValue lv;
		if (cl != null && (lv = labelValue(cl, e)) != null) {
			labelService.addLabelValueToCard(cl.getId(), cardId(e), lv, user, time);
		} else {
			insertLabelEvent(e, event, time, EventType.LABEL_CREATE);
		}
	}

}
