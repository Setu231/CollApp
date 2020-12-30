
package io.collapp.query;

import ch.digitalfondue.npjt.Bind;
import ch.digitalfondue.npjt.Query;
import ch.digitalfondue.npjt.QueryRepository;
import io.collapp.model.ApiHook;
import io.collapp.model.ApiHookNameAndVersion;

import java.util.List;

@QueryRepository
public interface ApiHookQuery {

	@Query("select API_HOOK_NAME, API_HOOK_VERSION from LA_API_HOOK where API_HOOK_ENABLED = true and API_HOOK_TYPE = :type")
	List<ApiHookNameAndVersion> findAllEnabled(@Bind("type") ApiHook.Type type);

    @Query("select API_HOOK_NAME, API_HOOK_VERSION from LA_API_HOOK where API_HOOK_NAME = :name and API_HOOK_ENABLED = true and API_HOOK_TYPE = :type")
    List<ApiHookNameAndVersion> findEnabledByNameAndType(@Bind("name") String name, @Bind("type") ApiHook.Type type);

	@Query("select API_HOOK_ENABLED from LA_API_HOOK where API_HOOK_NAME = :name")
	boolean findStatusByName(@Bind("name") String name);

	@Query("select API_HOOK_NAME, API_HOOK_SCRIPT, API_HOOK_CONFIGURATION, API_HOOK_ENABLED, API_HOOK_TYPE, API_HOOK_PROJECTS, API_HOOK_VERSION, API_HOOK_METADATA from LA_API_HOOK where API_HOOK_NAME in (:names)")
	List<ApiHook> findByNames(@Bind("names") List<String> names);

	@Query("select API_HOOK_NAME, API_HOOK_SCRIPT, API_HOOK_CONFIGURATION, API_HOOK_ENABLED, API_HOOK_TYPE, API_HOOK_PROJECTS, API_HOOK_VERSION, API_HOOK_METADATA from LA_API_HOOK order by API_HOOK_NAME asc")
	List<ApiHook> findAll();

	@Query("delete from LA_API_HOOK where API_HOOK_NAME = :name")
	int delete(@Bind("name") String name);

	@Query("insert into LA_API_HOOK(API_HOOK_NAME, API_HOOK_SCRIPT, API_HOOK_CONFIGURATION, API_HOOK_ENABLED, API_HOOK_TYPE, API_HOOK_PROJECTS, API_HOOK_VERSION, API_HOOK_METADATA) "
			+ " values (:name, :script, :configuration, :enabled, :type, :projects, 0, :metadata)")
	int insert(@Bind("name") String name, @Bind("script") String script, @Bind("configuration") String configuration,
			@Bind("enabled") boolean enabled, @Bind("type") ApiHook.Type type, @Bind("projects") String projects, @Bind("metadata") String metadata);

	@Query("update LA_API_HOOK set API_HOOK_SCRIPT = :script, API_HOOK_CONFIGURATION = :configuration, API_HOOK_METADATA = :metadata, "
			+ " API_HOOK_ENABLED = :enabled , API_HOOK_TYPE = :type, API_HOOK_PROJECTS = :projects, API_HOOK_VERSION = API_HOOK_VERSION+1 "
			+ " where API_HOOK_NAME = :name")
	int update(@Bind("name") String name, @Bind("script") String script, @Bind("configuration") String configuration,
			@Bind("enabled") boolean enabled, @Bind("type") ApiHook.Type type, @Bind("projects") String projects, @Bind("metadata") String metadata);

	@Query("update LA_API_HOOK set API_HOOK_ENABLED = :enabled, API_HOOK_VERSION = API_HOOK_VERSION+1 where API_HOOK_NAME = :name")
	int enable(@Bind("name") String name, @Bind("enabled") boolean enabled);

	@Query("update LA_API_HOOK set API_HOOK_NAME = :newName, API_HOOK_VERSION = API_HOOK_VERSION+1  where API_HOOK_NAME = :name")
	int rename(@Bind("name") String name, @Bind("newName") String newName);
}
