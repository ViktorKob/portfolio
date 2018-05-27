package net.thomas.portfolio.service_commons.services;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_DATA_TYPE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_REFERENCES;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_STATISTICS;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.INVERTED_INDEX_LOOKUP;
import static net.thomas.portfolio.services.Service.HBASE_INDEXING_SERVICE;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.core.ParameterizedTypeReference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;

import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class HbaseIndexModelAdaptorImpl implements HbaseIndexModelAdaptor {

	private final HttpRestClient client;
	private final HbaseIndexSchema schema;
	private final Cache<DataTypeId, DataType> dataTypeCache;

	public HbaseIndexModelAdaptorImpl(HttpRestClient client) {
		this.client = client;
		schema = client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class);
		dataTypeCache = newBuilder().refreshAfterWrite(10, MINUTES)
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
	public Collection<String> getDocumentTypes() {
		return schema.getDocumentTypes();
	}

	@Override
	public Collection<String> getSelectorTypes() {
		return schema.getSelectorTypes();
	}

	@Override
	public Collection<String> getIndexedDocumentTypes(String selectorType) {
		return schema.getIndexableDocumentTypes(selectorType);
	}

	@Override
	public Collection<String> getIndexedRelations(String selectorType) {
		return schema.getIndexableRelations(selectorType);
	}

	@Override
	public Collection<Field> getDataTypeFields(String dataType) {
		return schema.getFieldsForDataType(dataType);
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
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_DATA_TYPE, DataType.class, id);
	}

	@Override
	public Collection<Reference> getReferences(DataTypeId documentId) {
		final ParameterizedTypeReference<Collection<Reference>> typeReference = new ParameterizedTypeReference<Collection<Reference>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_REFERENCES, typeReference, documentId);
	}

	@Override
	public Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId) {
		final ParameterizedTypeReference<Map<StatisticsPeriod, Long>> typeReference = new ParameterizedTypeReference<Map<StatisticsPeriod, Long>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_STATISTICS, typeReference, selectorId);
	}

	@Override
	public List<DocumentInfo> invertedIndexLookup(InvertedIndexLookupRequest request) {
		final ParameterizedTypeReference<List<DocumentInfo>> responseType = new ParameterizedTypeReference<List<DocumentInfo>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, INVERTED_INDEX_LOOKUP, responseType, request.getGroups());
	}
}