package net.thomas.portfolio.shared_objects.hbase_index.schema;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public interface HbaseModelAdaptor {

	// TODO[Thomas]: Move to other class
	// Parser<String, Selector> getSimpleRepresentationParsers();
	// Selector getDataTypeBySimpleRepresentation(String type, String simpleRepresentation);

	/*** Schema ***/
	boolean isSimpleRepresentable(String type);

	boolean isSelector(String type);

	boolean isDocument(String type);

	Collection<String> getDataTypes();

	Collection<Field> getDataTypeFields(String dataType);

	Collection<Indexable> getIndexables(String selector);

	/*** Data ***/
	DataType getDataTypeByUid(String type, String uid);

	Collection<Reference> getReferences(Document document);

	Map<StatisticsPeriod, Long> getStatistics(Selector selector);

	List<Document> doSearch(SelectorSearch search, Indexable indexable);
}