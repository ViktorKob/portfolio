package net.thomas.portfolio.hbase_index;

import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATATYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class HbaseModelAdaptorImpl implements HbaseModelAdaptor {

	private final HttpRestClient client;
	private final HbaseIndexSchema schema;

	// private final Parser<String, Selector> simpleRepresentationParserLibrary;
	// private final Renderer<String> headlineRendererLibrary;
	// private final Renderer<String> simpleRepresentationRendererLibrary;

	public HbaseModelAdaptorImpl(HttpRestClient client) {
		this.client = client;
		schema = client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HbaseIndexSchemaImpl.class);
		// simpleRepresentationParserLibrary = new SampleModelSimpleRepresentationParserLibrary();
		// dateConverter = new DateConverter.SimpleDateConverter();
		// headlineRendererLibrary = new SampleModelHeadlineRendererLibrary(this);
		// simpleRepresentationRendererLibrary = new SampleModelSimpleRepresentationRendererLibrary();
	}

	// @Override
	// public Renderer<String> getHeadlineRenderers() {
	// return headlineRendererLibrary;
	// }
	//
	// @Override
	// public Renderer<String> getSimpleRepresentationRenderers() {
	// return simpleRepresentationRendererLibrary;
	// }

	@Override
	public boolean isSimpleRepresentable(String dataType) {
		return schema.getSimpleRepresentableTypes()
			.contains(dataType);
	}

	@Override
	public boolean isSelector(String dataType) {
		return schema.getSelectorTypes()
			.contains(dataType);
	}

	@Override
	public boolean isDocument(String dataType) {
		return schema.getDocumentTypes()
			.contains(dataType);
	}

	@Override
	public Collection<String> getDataTypes() {
		return schema.getDataTypes();
	}

	@Override
	public Collection<Field> getDataTypeFields(String dataType) {
		return schema.getFieldsForDataType(dataType);
	}

	@Override
	public Collection<Indexable> getIndexables(String selector) {
		return schema.getIndexables(selector);
	}

	@Override
	public DataType getDataTypeByUid(String type, String uid) {
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATATYPE, DataType.class, new PreSerializedParameter("type", type),
				new PreSerializedParameter("uid", uid));
	}

	// @Override
	// public Parser<String, Selector> getSimpleRepresentationParsers() {
	// return simpleRepresentationParserLibrary;
	// }
	//
	// @Override
	// public Selector getDataTypeBySimpleRepresentation(String type, String simpleRepresentation) {
	// return simpleRepresentationParserLibrary.parse(type, simpleRepresentation);
	// }

	@Override
	public Collection<Reference> getReferences(Document document) {
		// return index.getReferences(document);
		return null;
	}

	@Override
	public Map<StatisticsPeriod, Long> getStatistics(Selector selector) {
		// return index.getStatistics(selector);
		return null;
	}

	@Override
	public List<Document> doSearch(SelectorSearch search, Indexable indexable) {
		// final Iterator<Document> documents = index.invertedIndexLookup(search.selector, indexable)
		// .iterator();
		// final List<Document> result = new LinkedList<>();
		// if (search.before != null) {
		// @SuppressWarnings("unused")
		// Document next;
		// while (documents.hasNext() && (next = documents.next()).getTimeOfEvent() > search.before) {
		// }
		// }
		// int offset = search.offset;
		// while (documents.hasNext() && offset > 0) {
		// documents.next();
		// offset--;
		// }
		// final long after = search.after != null ? search.after : 0;
		// while (documents.hasNext() && result.size() < search.limit) {
		// final Document next = documents.next();
		// if (next.getTimeOfEvent() > after) {
		// result.add(next);
		// } else {
		// break;
		// }
		// }
		// return result;
		return null;
	}
}