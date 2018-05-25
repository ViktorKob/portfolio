package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter;

public interface HbaseIndexSchema {

	Collection<String> getDataTypes();

	Collection<String> getDocumentTypes();

	Collection<String> getSelectorTypes();

	Collection<String> getSimpleRepresentableTypes();

	Collection<Indexable> getIndexables(String selectorType, IndexableFilter... filters);

	Collection<Field> getFieldsForDataType(String dataType);

	Field getFieldForIndexable(Indexable indexable);

	String calculateUid(String type, String simpleRep);
}