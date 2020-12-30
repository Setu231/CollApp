
package io.collapp.service.config;

import io.collapp.common.collappEnvironment;
import io.collapp.config.DataSourceConfig;
import io.collapp.service.DatabaseMigrator;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.mock.env.MockEnvironment;

import javax.sql.DataSource;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class TestServiceConfig {

	@Bean(destroyMethod = "close")
	public DataSource getDataSource(collappEnvironment env) throws URISyntaxException {
		return new DataSourceConfig().getDataSource(env);
	}

	@Bean
	public DatabaseMigrator migrator(collappEnvironment env, DataSource dataSource) {
		return new DatabaseMigrator(env, dataSource, DataSourceConfig.LATEST_STABLE_VERSION);
	}

	@Bean
	public collappEnvironment collappEnvironment() {
		MockEnvironment m = new MockEnvironment();
		String dialect = System.getProperty("datasource.dialect", "HSQLDB");
		for (Entry<String, String> kv : datasourceConf().get(dialect).entrySet()) {
			m.setProperty(kv.getKey(), kv.getValue());
		}
		return new collappEnvironment(m);
	}

	@Bean
    public SimpMessageSendingOperations emitter() {
	    return Mockito.mock(SimpMessageSendingOperations.class);
    }

	private static Map<String, Map<String, String>> datasourceConf() {
		Map<String, Map<String, String>> r = new HashMap<>();
		r.put("HSQLDB", c("HSQLDB", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:collapp", "sa", ""));
		r.put("MYSQL", c("MYSQL", "com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/collapp?useUnicode=true&characterEncoding=utf-8", "root", ""));
		r.put("PGSQL", c("PGSQL", "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/collapp", "postgres", "password"));
		r.put("PGSQL-TRAVIS", c("PGSQL", "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/collapp", "postgres", ""));
		return r;
	}

	private static Map<String, String> c(String dialect, String driver, String url, String username, String password) {
		Map<String, String> c = new HashMap<>();
		c.put("datasource.dialect", dialect);
		c.put("datasource.driver", driver);
		c.put("datasource.url", url);
		c.put("datasource.username", username);
		c.put("datasource.password", password);
		return c;
	}

}
