package net.thomas.portfolio.shared_objects.adaptors;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public interface HbaseModelAdaptor {

	/*** Schema ***/
	boolean isSimpleRepresentable(String type);

	DataTypeId getIdFromSimpleRep(String type, String simpleRep);

	boolean isSelector(String type);

	boolean isDocument(String type);

	Collection<String> getDataTypes();

	Collection<Field> getDataTypeFields(String dataType);

	Collection<Indexable> getIndexables(String selector);

	/*** Data ***/

	DataType getDataType(DataTypeId id);

	Collection<Reference> getReferences(DataTypeId id);

	Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId);

	List<DocumentInfo> invertedIndexLookup(SelectorSearch search, Indexable indexable);

}