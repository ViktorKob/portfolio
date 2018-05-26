package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public interface HbaseIndex {

	DataType getDataType(DataTypeId id);

	List<Document> invertedIndexLookup(DataTypeId selectorId, Indexable indexable);

	Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId);

	Collection<Reference> getReferences(DataTypeId documentId);

}
