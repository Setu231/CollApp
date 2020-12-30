
package io.collapp.service.importexport;

import io.collapp.model.Event;
import io.collapp.model.EventFull;
import io.collapp.model.ImportContext;
import io.collapp.model.User;
import io.collapp.service.CardDataService;
import io.collapp.service.CardRepository;
import io.collapp.service.UserRepository;

import java.nio.file.Path;
import java.util.Date;

class DescriptionCreateUpdate extends AbstractProcessEvent {

	DescriptionCreateUpdate(CardRepository cardRepository, UserRepository userRepository,
			CardDataService cardDataService) {
		super(cardRepository, userRepository, cardDataService);
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		cardDataService.updateDescription(cardId(e), e.getContent(), time, user.getId());
	}

}
