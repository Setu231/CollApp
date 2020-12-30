
package io.collapp.service.importexport;

import io.collapp.model.*;
import io.collapp.service.CardDataService;
import io.collapp.service.CardRepository;
import io.collapp.service.UserRepository;

import java.nio.file.Path;
import java.util.Date;

class ActionListCreate extends AbstractProcessEvent {

	ActionListCreate(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService) {
		super(cardRepository, userRepository, cardDataService);
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		CardData cd = cardDataService.createActionList(cardId(e), e.getContent(), user.getId(), time);
		context.getActionListId().put(event.getDataId(), cd.getId());
	}

}
