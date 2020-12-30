
package io.collapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handle the export/import part of collapp.
 */
@Service
@Transactional(readOnly = true, timeout = 5000000)
public class ExportImportService {

    private final collappImporter importer;
    private final collappExporter exporter;
    private final AtomicBoolean isImporting = new AtomicBoolean(false);

    public ExportImportService(collappExporter exporter, collappImporter importer) {
        this.importer = importer;
        this.exporter = exporter;
    }

    public void exportData(OutputStream os) throws IOException {
        exporter.exportData(os);
    }

    @Transactional(readOnly = false)
    public void importData(boolean overrideConfiguration, Path tempFile) {
        try {
            if(!isImporting.compareAndSet(false, true)) {
                throw new IllegalStateException("Cannot do more than one import at a time");
            }
            importer.importData(overrideConfiguration, tempFile);
        } finally {
            isImporting.set(false);
        }
    }

    public boolean isImporting() {
        return isImporting.get();
    }
}
