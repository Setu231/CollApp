
package io.collapp.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Constants {

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";


	public static final String SYSTEM_LABEL_ASSIGNED = "ASSIGNED";
	public static final String SYSTEM_LABEL_DUE_DATE = "DUE_DATE";
	public static final String SYSTEM_LABEL_MILESTONE = "MILESTONE";
	public static final String SYSTEM_LABEL_WATCHED_BY = "WATCHED_BY";


	public static final Set<String> RESERVED_SYSTEM_LABELS_NAME = Collections.unmodifiableSet(new HashSet<>(Arrays
			.asList(SYSTEM_LABEL_ASSIGNED, SYSTEM_LABEL_DUE_DATE, SYSTEM_LABEL_MILESTONE, SYSTEM_LABEL_WATCHED_BY)));
}
