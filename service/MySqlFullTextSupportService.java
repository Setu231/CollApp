
package io.collapp.service;

import io.collapp.query.MySqlFullTextSupportQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(propagation = Propagation.NESTED)
public class MySqlFullTextSupportService {

	private static final Logger LOG = LogManager.getLogger();

	private final MySqlFullTextSupportQuery queries;


	public MySqlFullTextSupportService(MySqlFullTextSupportQuery queries) {
		this.queries = queries;
	}

	public void syncNewCards() {
		int rowAffected = queries.syncNewCards();
		LOG.debug("syncNewCards : updated {} row", rowAffected);
	}

	public void syncUpdatedCards() {
		int rowAffected = queries.syncUpdatedCards();
		LOG.debug("syncUpdatedCards : updated {} row", rowAffected);
	}

	public void syncNewCardData() {
		int rowAffected = queries.syncNewCardData();
		LOG.debug("syncNewCardData : updated {} row", rowAffected);
	}

	public void syncUpdatedCardData() {
		int rowAffected = queries.syncUpdatedCardData();
		LOG.debug("syncUpdatedCardData : updated {} row", rowAffected);
	}

}
