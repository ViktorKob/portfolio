package net.thomas.portfolio.hbase_index.fake;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;

public interface HBaseIndexSchemaSerialization {
	Map<String, Map<String, Field>> getDataTypeFields();

	void setDataTypeFields(Map<String, Map<String, Field>> dataTypeFields);

	Set<String> getDataTypes();

	void setDataTypes(Set<String> dataTypes);

	Set<String> getDocumentTypes();

	void setDocumentTypes(Set<String> documentTypes);

	Set<String> getSelectorTypes();

	void setSelectorTypes(Set<String> selectorTypes);

	Set<String> getSimpleRepresentableTypes();

	void setSimpleRepresentableTypes(Set<String> simpleRepresentableTypes);

	Map<String, List<Indexable>> getIndexables();

	void setIndexables(Map<String, List<Indexable>> indexables);
}
