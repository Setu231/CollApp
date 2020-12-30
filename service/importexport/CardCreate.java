
package io.collapp.service.importexport;

import io.collapp.model.Event;
import io.collapp.model.EventFull;
import io.collapp.model.ImportContext;
import io.collapp.model.User;
import io.collapp.service.CardDataService;
import io.collapp.service.CardRepository;
import io.collapp.service.CardService;
import io.collapp.service.UserRepository;

import java.nio.file.Path;
import java.util.Date;

class CardCreate extends AbstractProcessEvent {

	private final CardService cardService;

	CardCreate(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService,
			CardService cardService) {
		super(cardRepository, userRepository, cardDataService);
		this.cardService = cardService;
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		Integer columnId = context.getColumns().get(e.getEvent().getColumnId());
		if (columnId != null) {
			cardService.createCard(event.getValueString(), columnId, time, user);
		}
	}

}
