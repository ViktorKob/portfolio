package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATA_TYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_REFERENCES;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_STATISTICS;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.INVERTED_INDEX_LOOKUP;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.shared_objects.SelectorSearch;
import net.thomas.portfolio.shared_objects.adaptors.HbaseModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class HbaseModelAdaptorImpl implements HbaseModelAdaptor {

	private final HttpRestClient client;
	private final HbaseIndexSchema schema;

	public HbaseModelAdaptorImpl(HttpRestClient client) {
		this.client = client;
		schema = client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class);
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
	public Collection<Reference> getReferences(String type, String uid) {
		final ParameterizedTypeReference<Collection<Reference>> typeReference = new ParameterizedTypeReference<Collection<Reference>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_REFERENCES, typeReference, new PreSerializedParameter("type", type),
				new PreSerializedParameter("uid", uid));
	}

	@Override
	public Map<StatisticsPeriod, Long> getStatistics(Selector selector) {
		final ParameterizedTypeReference<Map<StatisticsPeriod, Long>> typeReference = new ParameterizedTypeReference<Map<StatisticsPeriod, Long>>() {
		};
		final DataTypeId id = selector.getId();
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_STATISTICS, typeReference, new PreSerializedParameter("type", id.getType()),
				new PreSerializedParameter("uid", id.getUid()));
	}

	@Override
	public List<DocumentInfo> invertedIndexLookup(SelectorSearch search, Indexable indexable) {
		final ParameterizedTypeReference<List<DocumentInfo>> responseType = new ParameterizedTypeReference<List<DocumentInfo>>() {
		};
		final DataTypeId id = search.selector.getId();
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, INVERTED_INDEX_LOOKUP, responseType, new PreSerializedParameter("type", id.getType()),
				new PreSerializedParameter("uid", id.getUid()));
	}
}