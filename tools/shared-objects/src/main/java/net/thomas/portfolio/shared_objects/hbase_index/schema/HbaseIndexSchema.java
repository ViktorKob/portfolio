package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface HbaseIndexSchema {

	Collection<String> getDataTypes();

	Collection<String> getDocumentTypes();

	Collection<String> getSelectorTypes();

	Collection<String> getSimpleRepresentableTypes();

	Collection<Field> getFieldsForDataType(String dataType);

	String calculateUid(String type, String simpleRep);

	Collection<String> getIndexableDocumentTypes(String selectorType);

	Collection<String> getIndexableRelations(String selectorType);
}