
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

class CardUpdate extends AbstractProcessEvent {

	private final CardService cardService;

	CardUpdate(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService,
			CardService cardService) {
		super(cardRepository, userRepository, cardDataService);
		this.cardService = cardService;
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		cardService.updateCardName(cardId(e), event.getValueString(), user, time);
	}

}
