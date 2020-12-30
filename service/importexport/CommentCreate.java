
package io.collapp.service.importexport;

import io.collapp.model.*;
import io.collapp.service.CardDataService;
import io.collapp.service.CardRepository;
import io.collapp.service.UserRepository;

import java.nio.file.Path;
import java.util.Date;

class CommentCreate extends AbstractProcessEvent {

	CommentCreate(CardRepository cardRepository, UserRepository userRepository, CardDataService cardDataService) {
		super(cardRepository, userRepository, cardDataService);
	}

	@Override
	void process(EventFull e, Event event, Date time, User user, ImportContext context, Path tempFile) {
		CardData cd = cardDataService.createComment(cardId(e), e.getContent(), time, user.getId());
		context.getCommentsId().put(event.getDataId(), cd.getId());
	}

}
