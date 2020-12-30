
package io.collapp.service.importexport;

import io.collapp.model.Event;
import io.collapp.model.Event.EventType;
import io.collapp.model.EventFull;
import io.collapp.model.ImportContext;
import io.collapp.model.User;
import io.collapp.service.*;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

@Component
public class ImportEventProcessor implements ImportEvent {

	private final UserRepository userRepository;
	private final Map<EventType, AbstractProcessEvent> eventProcessors;


	public ImportEventProcessor(CardRepository cardRepository, UserRepository userRepository,
			CardDataService cardDataService, CardService cardService, EventRepository eventRepository,

			LabelService labelService, CardLabelRepository cardLabelRepository, BoardRepository boardRepository) {

		this.userRepository = userRepository;

		Map<EventType, AbstractProcessEvent> mapping = new EnumMap<>(EventType.class);

		//
		mapping.put(EventType.CARD_CREATE, new CardCreate(cardRepository, userRepository, cardDataService, cardService));
		mapping.put(EventType.CARD_UPDATE, new CardUpdate(cardRepository, userRepository, cardDataService, cardService));
		mapping.put(EventType.CARD_MOVE, new CardMove(cardRepository, userRepository, cardDataService, cardService));

		AbstractProcessEvent abtProcessor = new CardArchiveBacklogTrash(cardRepository, userRepository,
				cardDataService, cardService, eventRepository);
		mapping.put(EventType.CARD_ARCHIVE, abtProcessor);
		mapping.put(EventType.CARD_BACKLOG, abtProcessor);
		mapping.put(EventType.CARD_TRASH, abtProcessor);

		AbstractProcessEvent descProcessor = new DescriptionCreateUpdate(cardRepository, userRepository,
				cardDataService);
		mapping.put(EventType.DESCRIPTION_CREATE, descProcessor);
		mapping.put(EventType.DESCRIPTION_UPDATE, descProcessor);

		mapping.put(EventType.COMMENT_CREATE, new CommentCreate(cardRepository, userRepository, cardDataService));
		mapping.put(EventType.COMMENT_UPDATE, new CommentUpdate(cardRepository, userRepository, cardDataService));
		mapping.put(EventType.COMMENT_DELETE, new CommentDelete(cardRepository, userRepository, cardDataService));

		mapping.put(EventType.ACTION_LIST_CREATE, new ActionListCreate(cardRepository, userRepository, cardDataService));
		mapping.put(EventType.ACTION_LIST_DELETE, new ActionListDelete(cardRepository, userRepository, cardDataService));

		mapping.put(EventType.ACTION_ITEM_CREATE, new ActionItemCreate(cardRepository, userRepository, cardDataService));
		mapping.put(EventType.ACTION_ITEM_DELETE, new ActionItemDelete(cardRepository, userRepository, cardDataService));
		mapping.put(EventType.ACTION_ITEM_MOVE, new ActionItemMove(cardRepository, userRepository, cardDataService));

		AbstractProcessEvent actionItemCheckUncheck = new ActionItemCheckUncheck(cardRepository, userRepository,
				cardDataService);
		mapping.put(EventType.ACTION_ITEM_CHECK, actionItemCheckUncheck);
		mapping.put(EventType.ACTION_ITEM_UNCHECK, actionItemCheckUncheck);

		mapping.put(EventType.LABEL_CREATE, new LabelCreate(cardRepository, userRepository, cardDataService,
				labelService, cardLabelRepository, boardRepository, eventRepository));
		mapping.put(EventType.LABEL_DELETE, new LabelDelete(cardRepository, userRepository, cardDataService,
				labelService, cardLabelRepository, boardRepository, eventRepository));

		mapping.put(EventType.FILE_UPLOAD, new FileUpload(cardRepository, userRepository, cardDataService));
		mapping.put(EventType.FILE_DELETE, new FileDelete(cardRepository, userRepository, cardDataService));
		//
		eventProcessors = Collections.unmodifiableMap(mapping);
	}

	public void processEvent(EventFull e, ImportContext context, Path tempFile) {
		Event event = e.getEvent();

		if (eventProcessors.containsKey(event.getEvent())) {
			User user = userRepository.findUserByName(e.getUserProvider(), e.getUsername());
			Date time = event.getTime();

			eventProcessors.get(event.getEvent()).process(e, event, time, user, context, tempFile);
		}
	}
}
