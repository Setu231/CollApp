
package io.collapp.query;

import ch.digitalfondue.npjt.Bind;
import ch.digitalfondue.npjt.Query;
import ch.digitalfondue.npjt.QueryRepository;
import io.collapp.model.ListValueMetadata;

import java.util.Collection;
import java.util.List;

@QueryRepository
public interface ListValueMetadataQuery {

	@Query("INSERT INTO LA_LIST_VALUE_METADATA(LVM_LABEL_LIST_VALUE_ID_FK, LVM_KEY, LVM_VALUE) "
			+ "VALUES (:labelListValueId, :key, :value)")
	int insert(@Bind("labelListValueId") int labelListValueId, @Bind("key") String key, @Bind("value") String value);

	@Query("DELETE FROM LA_LIST_VALUE_METADATA WHERE LVM_LABEL_LIST_VALUE_ID_FK = :labelListValueId AND LVM_KEY = :key")
	int delete(@Bind("labelListValueId") int labelListValueId, @Bind("key") String key);

	@Query("DELETE FROM LA_LIST_VALUE_METADATA WHERE LVM_LABEL_LIST_VALUE_ID_FK = :labelListValueId")
	int deleteAllWithLabelListValueId(@Bind("labelListValueId") int labelListValueId);

	@Query("UPDATE LA_LIST_VALUE_METADATA SET LVM_VALUE = :value WHERE LVM_LABEL_LIST_VALUE_ID_FK = :labelListValueId AND LVM_KEY = :key")
	int update(@Bind("labelListValueId") int labelListValueId, @Bind("key") String key, @Bind("value") String value);

	@Query("SELECT * FROM LA_LIST_VALUE_METADATA WHERE LVM_LABEL_LIST_VALUE_ID_FK = :labelListValueId")
	List<ListValueMetadata> findByLabelListValueId(@Bind("labelListValueId") int labelListValueId);

	@Query("SELECT * FROM LA_LIST_VALUE_METADATA WHERE LVM_LABEL_LIST_VALUE_ID_FK IN (:labelListValueIds)")
	List<ListValueMetadata> findByLabelListValueIds(@Bind("labelListValueIds") Collection<Integer> labelListValueId);

	@Query("SELECT * FROM LA_LIST_VALUE_METADATA WHERE LVM_LABEL_LIST_VALUE_ID_FK = :labelListValueId AND LVM_KEY = :key")
	ListValueMetadata findByLabelListValueIdAndKey(@Bind("labelListValueId") int labelListValueId, @Bind("key") String key);

	@Query("SELECT COUNT(*) FROM LA_CARD_LABEL_VALUE WHERE CARD_LABEL_VALUE_LIST_VALUE_FK = :id")
	Integer countUse(@Bind("id") int labelListValueId);
}
