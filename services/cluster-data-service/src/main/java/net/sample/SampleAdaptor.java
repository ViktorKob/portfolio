package net.sample;

import static net.sample.SampleModel.DATA_TYPES;
import static net.sample.SampleModel.DATA_TYPE_FIELDS;
import static net.sample.SampleModel.DOCUMENT_TYPES;
import static net.sample.SampleModel.INDEXABLES;
import static net.sample.SampleModel.SELECTOR_TYPES;
import static net.sample.SampleModel.SIMPLE_REPRESENTATION_TYPES;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.graphql.fetchers.ModelAdaptor;
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
import net.model.util.HeadlineRendererLibrary;
import net.model.util.Parser;
import net.model.util.SimpleRepresentationParserLibrary;
import net.model.util.SimpleRepresentationRendererLibrary;

public class SampleAdaptor implements ModelAdaptor {

	private final SampleStorage storage;
	private final Renderer<String> headlineRendererLibrary;
	private final Parser<String, Selector> simpleRepresentationParserLibrary;
	private final Renderer<String> simpleRepresentationRendererLibrary;
	private final DateConverter dateConverter;

	public SampleAdaptor(SampleStorage storage) {
		this.storage = storage;
		dateConverter = new DateConverter.SimpleDateConverter();
		headlineRendererLibrary = new HeadlineRendererLibrary(this);
		simpleRepresentationParserLibrary = new SimpleRepresentationParserLibrary();
		simpleRepresentationRendererLibrary = new SimpleRepresentationRendererLibrary();
	}

	@Override
	public DateConverter getDateConverter() {
		return dateConverter;
	}

	@Override
	public Renderer<String> getHeadlineRenderers() {
		return headlineRendererLibrary;
	}

	@Override
	public Parser<String, Selector> getSimpleRepresentationParsers() {
		return simpleRepresentationParserLibrary;
	}

	@Override
	public Renderer<String> getSimpleRepresentationRenderers() {
		return simpleRepresentationRendererLibrary;
	}

	@Override
	public boolean isSimpleRepresentable(String dataType) {
		return SIMPLE_REPRESENTATION_TYPES.contains(dataType);
	}

	@Override
	public boolean isSelector(String dataType) {
		return SELECTOR_TYPES.contains(dataType);
	}

	@Override
	public boolean isDocument(String dataType) {
		return DOCUMENT_TYPES.contains(dataType);
	}

	@Override
	public Set<String> getDataTypes() {
		return DATA_TYPES;
	}

	@Override
	public Collection<Field> getDataTypeFields(String dataType) {
		return DATA_TYPE_FIELDS.get(dataType).values();
	}

	@Override
	public List<Indexable> getIndexablesFor(String dataType) {
		return INDEXABLES.get(dataType);
	}

	@Override
	public DataType getDataTypeByUid(String type, String uid) {
		return storage.getDataType(type, uid);
	}

	@Override
	public Selector getDataTypeBySimpleRepresentation(String type, String simpleRepresentation) {
		return simpleRepresentationParserLibrary.parse(type, simpleRepresentation);
	}

	@Override
	public List<Reference> getReferencesFor(Document document) {
		return storage.getReferences(document);
	}

	@Override
	public Map<StatisticsPeriod, Long> getStatisticsFor(Selector selector) {
		return storage.getStatistics(selector);
	}

	@Override
	public List<Document> doSearch(SelectorSearch search, Indexable indexable) {
		final Iterator<Document> documents = storage.invertedIndexLookup(search.selector, indexable).iterator();
		final List<Document> result = new LinkedList<>();
		if (search.before != null) {
			@SuppressWarnings("unused")
			Document next;
			while (documents.hasNext() && (next = documents.next()).getTimeOfEvent() > search.before) {}
		}
		int offset = search.offset;
		while (documents.hasNext() && offset > 0) {
			documents.next();
			offset--;
		}
		final long after = search.after != null ? search.after : 0;
		while (documents.hasNext() && result.size() < search.limit) {
			final Document next = documents.next();
			if (next.getTimeOfEvent() > after) {
				result.add(next);
			} else {
				break;
			}
		}
		return result;
	}

	@Override
	public PreviousKnowledge getPreviousKnowledgeFor(Selector selector) {
		return storage.lookupPreviousKnowledgeFor(selector);
	}
}