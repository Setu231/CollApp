
package io.collapp.service.importexport;

import io.collapp.model.Event;
import io.collapp.model.EventFull;
import io.collapp.model.ImportContext;
import io.collapp.model.User;
import io.collapp.service.CardDataService;
import io.collapp.service.CardRepository;
import io.collapp.service.UserRepository;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;

class ActionItemMove extends AbstractProcessEvent {

	ActionItemMove(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService) {
		super(cardRepository, userRepository, cardDataService);
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		int actionItemId = context.getActionItemId().get(event.getDataId());
		cardDataService.moveActionItem(cardId(e), actionItemId, context.getActionListId().get(event.getNewDataId()),
				Collections.singletonList(actionItemId), user, time);
	}

}
