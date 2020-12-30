
package io.collapp.service;

import io.collapp.model.EventFull;
import io.collapp.model.ImportContext;

import java.nio.file.Path;

public interface ImportEvent {
	void processEvent(EventFull e, ImportContext context, Path tempFile);
}
