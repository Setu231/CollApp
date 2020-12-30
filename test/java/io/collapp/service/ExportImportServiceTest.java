
package io.collapp.service;

import io.collapp.config.PersistenceAndServiceConfig;
import io.collapp.service.ExportImportService;
import io.collapp.service.config.TestServiceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestServiceConfig.class, PersistenceAndServiceConfig.class })
@Transactional
public class ExportImportServiceTest {

	@Autowired
	private ExportImportService exportImportService;

	@Test
	public void testImportAndExport() throws IOException {
		Path tmp = Files.createTempFile(null, null);
		try (InputStream is = new ClassPathResource("io/collapp/export2.zip").getInputStream()) {
			Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
			exportImportService.importData(false, tmp);

			// TODO additional checks

			exportImportService.exportData(new ByteArrayOutputStream());
		} finally {
			if (tmp != null) {
				Files.deleteIfExists(tmp);
			}
		}

	}

}
