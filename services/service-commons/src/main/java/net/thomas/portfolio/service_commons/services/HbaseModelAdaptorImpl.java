package net.thomas.portfolio.service_commons.services;

import static java.util.concurrent.TimeUnit.MINUTES;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATA_TYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_REFERENCES;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_STATISTICS;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.INVERTED_INDEX_LOOKUP;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.core.ParameterizedTypeReference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

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
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class HbaseModelAdaptorImpl implements HbaseModelAdaptor {

	private final HttpRestClient client;
	private final HbaseIndexSchema schema;
	private final Cache<DataTypeId, DataType> dataTypeCache;

	public HbaseModelAdaptorImpl(HttpRestClient client) {
		this.client = client;
		schema = client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class);
		dataTypeCache = CacheBuilder.newBuilder()
			.refreshAfterWrite(10, MINUTES)
			.maximumSize(10000)
			.build(new CacheLoader<DataTypeId, DataType>() {
				@Override
				public DataType load(DataTypeId id) throws Exception {
					return fetchDataType(id);
				}
			});
	}

	@Override
	public boolean isSimpleRepresentable(String dataType) {
		return schema.getSimpleRepresentableTypes()
			.contains(dataType);
	}

	@Override
	public DataTypeId getIdFromSimpleRep(String type, String simpleRep) {
		return new DataTypeId(type, schema.calculateUid(type, simpleRep));
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
	public DataType getDataType(DataTypeId id) {
		try {
			return dataTypeCache.get(id, () -> {
				return fetchDataType(id);
			});
		} catch (final ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	private DataType fetchDataType(DataTypeId id) {
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATA_TYPE, DataType.class, new PreSerializedParameter("type", id.getType()),
				new PreSerializedParameter("uid", id.getUid()));
	}

	@Override
	public Collection<Reference> getReferences(DataTypeId documentId) {
		final ParameterizedTypeReference<Collection<Reference>> typeReference = new ParameterizedTypeReference<Collection<Reference>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_REFERENCES, typeReference, new PreSerializedParameter("type", documentId.getType()),
				new PreSerializedParameter("uid", documentId.getUid()));
	}

	@Override
	public Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId) {
		final ParameterizedTypeReference<Map<StatisticsPeriod, Long>> typeReference = new ParameterizedTypeReference<Map<StatisticsPeriod, Long>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_STATISTICS, typeReference, new PreSerializedParameter("type", selectorId.getType()),
				new PreSerializedParameter("uid", selectorId.getUid()));
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