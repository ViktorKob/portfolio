package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface HbaseIndexSchema {

	Collection<String> getDataTypes();

	Collection<String> getDocumentTypes();

	Collection<String> getSelectorTypes();

	Collection<String> getSimpleRepresentableTypes();

	Collection<Field> getFieldsForDataType(String dataType);

	String calculateUid(String type, String simpleRep);

	List<DataTypeId> getSelectorSuggestions(String selectorString);

	Collection<String> getIndexableDocumentTypes(String selectorType);

	Collection<String> getIndexableRelations(String selectorType);
}