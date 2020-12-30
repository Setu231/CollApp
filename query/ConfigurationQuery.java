
package io.collapp.query;

import ch.digitalfondue.npjt.Bind;
import ch.digitalfondue.npjt.Query;
import ch.digitalfondue.npjt.QueryRepository;
import io.collapp.model.ConfigurationKeyValue;

import java.util.List;
import java.util.Set;

@QueryRepository
public interface ConfigurationQuery {

	@Query("SELECT COUNT(*) FROM LA_CONF WHERE CONF_KEY = :key")
	Integer hasKeyDefined(@Bind("key") String key);

	@Query("SELECT * FROM LA_CONF WHERE CONF_KEY IN (:keys)")
	List<ConfigurationKeyValue> findConfigurationFor(@Bind("keys") Set<String> keys);

	@Query("SELECT CONF_VALUE FROM LA_CONF WHERE CONF_KEY = :key")
	List<String> getValue(@Bind("key") String key);

	@Query("INSERT INTO LA_CONF(CONF_KEY, CONF_VALUE) VALUES(:key, :value)")
	int set(@Bind("key") String key, @Bind("value") String value);

	@Query("UPDATE LA_CONF SET CONF_VALUE = :value WHERE CONF_KEY = :key")
	int update(@Bind("key") String key, @Bind("value") String value);

	@Query("SELECT * FROM LA_CONF ORDER BY CONF_KEY")
	List<ConfigurationKeyValue> findAll();

	@Query("DELETE FROM LA_CONF WHERE CONF_KEY = :key")
	int delete(@Bind("key") String key);
}
