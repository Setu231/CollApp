
package io.collapp.service.importexport;

import io.collapp.common.Constants;
import io.collapp.model.*;
import io.collapp.model.CardLabel.LabelDomain;
import io.collapp.model.CardLabel.LabelType;
import io.collapp.model.CardLabelValue.LabelValue;
import io.collapp.model.Event.EventType;
import io.collapp.service.*;

import java.util.Date;
import java.util.List;

abstract class AbstractProcessLabelEvent extends AbstractProcessEvent {

	protected final LabelService labelService;
	protected final CardLabelRepository cardLabelRepository;
	protected final BoardRepository boardRepository;
	private final EventRepository eventRepository;

	AbstractProcessLabelEvent(CardRepository cardRepository, UserRepository userRepository,
			CardDataService cardDataService, LabelService labelService, CardLabelRepository cardLabelRepository,
			BoardRepository boardRepository, EventRepository eventRepository) {
		super(cardRepository, userRepository, cardDataService);
		this.labelService = labelService;
		this.cardLabelRepository = cardLabelRepository;
		this.boardRepository = boardRepository;
		this.eventRepository = eventRepository;
	}

	protected CardLabelValue findCardLabelValueBy(EventFull e) {
		CardLabel cl = findLabelByEvent(e);
		if (cl != null) {
			LabelValue lv = labelValue(cl, e);
			if(lv == null) {
				return null;
			}
			List<CardLabelValue> r = cardLabelRepository.findLabelValueByLabelAndValue(cardId(e), cl, lv);
			return r.size() == 1 ? r.get(0) : null;
		}
		return null;
	}

	protected LabelValue labelValue(CardLabel cl, EventFull e) {
		Event event = e.getEvent();
		if (cl.getType() == LabelType.LIST) {
			List<LabelListValueWithMetadata> res = cardLabelRepository.findListValuesByLabelIdAndValue(cl.getId(), e.getEvent()
					.getValueString());
			return (res.size() == 1) ? new LabelValue(null, null, null, null, null, res.get(0).getId()) : null;
		} else if (cl.getType() == LabelType.USER) {
			return new LabelValue(null, null, null, null, userRepository.findUserByName(e.getLabelUserProvider(),
					e.getLabelUsername()).getId(), null);
		} else if (cl.getType() == LabelType.CARD) {
			return new LabelValue(null, null, null, cardRepository.findCardIdByBoardNameAndSeq(
					e.getLabelBoardShortName(), e.getLabelCardSequenceNumber()), null, null);
		} else {
			return new LabelValue(event.getValueString(), event.getValueTimestamp(), event.getValueInt(), null, null,
					null);
		}

	}

	protected CardLabel findLabelByEvent(EventFull e) {
		Board b = boardRepository.findBoardByShortName(e.getBoardShortName());
		LabelDomain domain = Constants.RESERVED_SYSTEM_LABELS_NAME.contains(e.getEvent().getLabelName()) ? LabelDomain.SYSTEM : LabelDomain.USER;
		List<CardLabel> r = cardLabelRepository.findLabelsByName(b.getProjectId(), e.getEvent().getLabelName(), domain);
		return r.isEmpty() ? null : r.get(0);
	}

	protected Integer fromLabelUsernameToUserId(EventFull e) {
		if (e.getLabelUsername() != null && e.getLabelCardSequenceNumber() != null
				&& userRepository.userExists(e.getLabelUserProvider(), e.getLabelUsername())) {
			return userRepository.findUserByName(e.getLabelUserProvider(), e.getLabelUsername()).getId();
		}
		return null;
	}

	protected Integer fromLabelCardToCardId(EventFull e) {
		if (e.getLabelBoardShortName() != null && e.getLabelCardSequenceNumber() != null
				&& cardRepository.existCardWith(e.getLabelBoardShortName(), e.getLabelCardSequenceNumber())) {
			return cardRepository.findCardIdByBoardNameAndSeq(e.getLabelBoardShortName(),
					e.getLabelCardSequenceNumber());
		}
		return null;
	}

	protected void insertLabelEvent(EventFull e, Event event, Date time, EventType eventType) {
		Integer labelCardId = fromLabelCardToCardId(e);
		Integer labeUserId = fromLabelUsernameToUserId(e);

		LabelValue labelValue = new LabelValue(event.getValueString(), event.getValueTimestamp(), event.getValueInt(),
				labelCardId, labeUserId, null);

		if ((event.getLabelType() == LabelType.CARD && labelCardId == null)
				|| (event.getLabelType() == LabelType.USER && labeUserId == null)) {
			return;
		}

		eventRepository.insertLabelEvent(event.getLabelName(), cardId(e), toUser(e).getId(), eventType, labelValue,
				event.getLabelType(), time);
	}

}
