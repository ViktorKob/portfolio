package net.graphql.fetchers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.model.DataType;
import net.model.data.Field;
import net.model.data.SelectorSearch;
import net.model.meta_data.Indexable;
import net.model.meta_data.PreviousKnowledge;
import net.model.meta_data.Reference;
import net.model.meta_data.Renderer;
import net.model.meta_data.StatisticsPeriod;
import net.model.types.Document;
import net.model.types.Selector;
import net.model.util.DateConverter;
import net.model.util.Parser;

public interface ModelAdaptor {

	/*** Utils ***/
	DateConverter getDateConverter();

	Renderer<String> getHeadlineRenderers();

	Parser<String, Selector> getSimpleRepresentationParsers();

	Renderer<String> getSimpleRepresentationRenderers();

	/*** Schema ***/
	boolean isSimpleRepresentable(String type);

	boolean isSelector(String type);

	boolean isDocument(String type);

	Set<String> getDataTypes();

	Collection<Field> getDataTypeFields(String dataType);

	List<Indexable> getIndexablesFor(String dataType);

	/*** Data ***/
	DataType getDataTypeByUid(String type, String uid);

	Selector getDataTypeBySimpleRepresentation(String type, String simpleRepresentation);

	List<Reference> getReferencesFor(Document document);

	Map<StatisticsPeriod, Long> getStatisticsFor(Selector selector);

	PreviousKnowledge getPreviousKnowledgeFor(Selector selector);

	List<Document> doSearch(SelectorSearch search, Indexable indexable);
}