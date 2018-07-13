package net.thomas.portfolio.service_commons.adaptors.impl;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.DOCUMENTS;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.ENTITIES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.INVERTED_INDEX;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.REFERENCES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SCHEMA;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SELECTORS;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.STATISTICS;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SUGGESTIONS;
import static net.thomas.portfolio.services.Service.HBASE_INDEXING_SERVICE;
import static org.springframework.http.HttpMethod.GET;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.common.services.ParameterGroup;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

@EnableCircuitBreaker
public class HbaseIndexModelAdaptorImpl implements HbaseIndexModelAdaptor {

	private static final ParameterGroup[] EMPTY_GROUP_LIST = new ParameterGroup[0];
	private HttpRestClient client;
	private HbaseIndexSchema schema;
	private Cache<DataTypeId, DataType> dataTypeCache;

	public void initialize(HttpRestClient client) {
		this.client = client;
		schema = client.loadUrlAsObject(HBASE_INDEXING_SERVICE, SCHEMA, GET, HbaseIndexSchemaImpl.class);
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
		final String uid = schema.calculateUid(type, simpleRep);
		if (uid != null) {
			return new DataTypeId(type, uid);
		} else {
			return null;
		}
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
	public Set<String> getIndexedDocumentTypes(String selectorType) {
		return schema.getIndexableDocumentTypes(selectorType);
	}

	@Override
	public Set<String> getIndexedRelations(String selectorType) {
		return schema.getIndexableRelations(selectorType);
	}

	@Override
	public Set<String> getAllIndexedRelations() {
		return schema.getAllIndexableRelations();
	}

	@Override
	public Fields getFieldsForDataType(String dataType) {
		return schema.getFieldsForDataType(dataType);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public List<DataTypeId> getSelectorSuggestions(String selectorString) {
		final ParameterizedTypeReference<List<DataTypeId>> responseType = new ParameterizedTypeReference<List<DataTypeId>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, () -> {
			return SELECTORS.getPath() + "/" + SUGGESTIONS.getPath() + "/" + selectorString + "/";
		}, GET, responseType, EMPTY_GROUP_LIST);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public DataType getDataType(DataTypeId id) {
		try {
			return dataTypeCache.get(id, () -> {
				return fetchDataType(id);
			});
		} catch (final InvalidCacheLoadException e) {
			if (e.getMessage()
				.contains("CacheLoader returned null for key")) {
				return null;
			} else {
				throw new RuntimeException("Unable to fetch data type", e);
			}
		} catch (final ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	private DataType fetchDataType(DataTypeId id) {
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, () -> {
			return ENTITIES.getPath() + "/" + id.getDti_type() + "/" + id.getDti_uid();
		}, GET, DataType.class);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Collection<Reference> getReferences(DataTypeId documentId) {
		final ParameterizedTypeReference<Collection<Reference>> responseType = new ParameterizedTypeReference<Collection<Reference>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, () -> {
			return DOCUMENTS.getPath() + "/" + documentId.getDti_type() + "/" + documentId.getDti_uid() + REFERENCES.getPath();
		}, GET, responseType, EMPTY_GROUP_LIST);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Map<StatisticsPeriod, Long> getStatistics(DataTypeId selectorId) {
		final ParameterizedTypeReference<Map<StatisticsPeriod, Long>> responseType = new ParameterizedTypeReference<Map<StatisticsPeriod, Long>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, () -> {
			return SELECTORS.getPath() + "/" + selectorId.getDti_type() + "/" + selectorId.getDti_uid() + STATISTICS.getPath();
		}, GET, responseType, EMPTY_GROUP_LIST);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public List<DocumentInfo> lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request) {
		final ParameterizedTypeReference<List<DocumentInfo>> responseType = new ParameterizedTypeReference<List<DocumentInfo>>() {
		};
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, () -> {
			final DataTypeId selectorId = request.getSelectorId();
			return SELECTORS.getPath() + "/" + selectorId.getDti_type() + "/" + selectorId.getDti_uid() + INVERTED_INDEX.getPath();
		}, GET, responseType, request.getGroups());
	}
}