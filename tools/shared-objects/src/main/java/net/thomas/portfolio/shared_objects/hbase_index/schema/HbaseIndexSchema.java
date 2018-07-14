package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface HbaseIndexSchema {

	Collection<String> getDataTypes();

	Collection<String> getDocumentTypes();

	Collection<String> getSelectorTypes();

	Collection<String> getSimpleRepresentableTypes();

	Fields getFieldsForDataType(String dataType);

	String calculateUid(String type, String simpleRep);

	List<DataTypeId> getSelectorSuggestions(String selectorString);

	Set<String> getIndexableDocumentTypes(String selectorType);

	Set<String> getIndexableRelations(String selectorType);

	Set<String> getAllIndexableRelations();

	Collection<Indexable> getIndexables(String selectorType);

	Field getFieldForIndexable(Indexable indexable);
}