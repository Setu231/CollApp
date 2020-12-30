
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

abstract class AbstractProcessEvent {

	protected final CardRepository cardRepository;
	protected final UserRepository userRepository;
	protected final CardDataService cardDataService;

	AbstractProcessEvent(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService) {
		this.cardRepository = cardRepository;
		this.userRepository = userRepository;
		this.cardDataService = cardDataService;
	}

	abstract void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile);

	protected int cardId(EventFull e) {
		return cardRepository.findCardIdByBoardNameAndSeq(e.getBoardShortName(), e.getCardSequenceNumber());
	}

	protected User toUser(EventFull e) {
		return userRepository.findUserByName(e.getUserProvider(), e.getUsername());
	}
}
