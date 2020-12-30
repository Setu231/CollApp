
package io.collapp.service;

import io.collapp.common.collappEnvironment;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;

import javax.sql.DataSource;

public class DatabaseMigrator {

	public DatabaseMigrator(collappEnvironment env, DataSource dataSource, MigrationVersion target) {
		if (canMigrate(env)) {
			doMigration(env, dataSource, target);
		}
	}

	private void doMigration(collappEnvironment env, DataSource dataSource, MigrationVersion version) {
		String sqlDialect = env.getRequiredProperty("datasource.dialect");
		Flyway migration = new Flyway();
		migration.setDataSource(dataSource);
		// FIXME remove the validation = false when the schemas will be stable
		migration.setValidateOnMigrate(false);
		//

		migration.setTarget(version);

		migration.setLocations("io/collapp/db/" + sqlDialect + "/");
		migration.migrate();
	}

	private boolean canMigrate(collappEnvironment env) {
		return !"true".equals(env.getProperty("datasource.disable.migration"));
	}
}
