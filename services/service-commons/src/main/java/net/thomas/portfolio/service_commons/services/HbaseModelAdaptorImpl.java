package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATA_TYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_REFERENCES;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_STATISTICS;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class HbaseModelAdaptorImpl implements HbaseModelAdaptor {

	private final HttpRestClient client;
	private final HbaseIndexSchema schema;

	// private final Parser<String, Selector> simpleRepresentationParserLibrary;

	public HbaseModelAdaptorImpl(HttpRestClient client) {
		this.client = client;
		schema = client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class);
		// simpleRepresentationParserLibrary = new SampleModelSimpleRepresentationParserLibrary();
	}

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
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATA_TYPE, DataType.class, new PreSerializedParameter("type", type),
				new PreSerializedParameter("uid", uid));
	}

	@Override
	public Selector getDataTypeBySimpleRep(String type, String simpleRep) {
		final String uid = schema.calculateUid(type, simpleRep);
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATA_TYPE, Selector.class, new PreSerializedParameter("type", type),
				new PreSerializedParameter("uid", uid));
	}

	@Override
	@SuppressWarnings("unchecked")
	public References getReferences(Document document) {
		return new References(client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_REFERENCES, LinkedList.class,
				new PreSerializedParameter("type", document.getType()), new PreSerializedParameter("uid", document.getUid())));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Statistics getStatistics(Selector selector) {
		return new Statistics(client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_STATISTICS, HashMap.class,
				new PreSerializedParameter("type", selector.getType()), new PreSerializedParameter("uid", selector.getUid())));
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