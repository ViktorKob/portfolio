package net.thomas.portfolio.shared_objects.hbase_index.schema;

import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;

public interface HbaseIndex extends Iterable<DataType> {

	Entities getSamples(String type, int amount);

	DataType getDataType(DataTypeId id);

	DocumentInfos invertedIndexLookup(DataTypeId selectorId, Indexable indexable);

	Statistics getStatistics(DataTypeId selectorId);

	References getReferences(DataTypeId documentId);

}
