package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public interface HbaseIndex {

	List<Document> invertedIndexLookup(Selector selector, Indexable indexable);

	Map<StatisticsPeriod, Long> getStatistics(Selector selector);

	Collection<Reference> getReferences(Document document);

	Datatype getDataType(String type, String uid);

}
