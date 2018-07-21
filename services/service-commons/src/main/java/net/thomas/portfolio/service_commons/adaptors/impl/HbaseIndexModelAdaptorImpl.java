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
import static net.thomas.portfolio.service_commons.network.ServiceEndpointBuilder.asEndpoint;
import static net.thomas.portfolio.services.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId.NULL_ID;
import static org.springframework.http.HttpMethod.GET;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

@EnableCircuitBreaker
public class HbaseIndexModelAdaptorImpl implements HttpRestClientInitializable, HbaseIndexModelAdaptor {

	private static final ParameterGroup[] EMPTY_GROUP_LIST = new ParameterGroup[0];
	private HttpRestClient client;
	private HbaseIndexSchema schema;
	private LoadingCache<DataTypeId, DataType> dataTypeCache;

	@Override
	public void initialize(HttpRestClient client) {
		this.client = client;
		while (schema == null) {
			try {
				schema = client.loadUrlAsObject(HBASE_INDEXING_SERVICE, SCHEMA, GET, HbaseIndexSchemaImpl.class);
			} catch (final RuntimeException e) {
				// We try again until we succeed or the service is closed from the outside
			}
		}
		dataTypeCache = newBuilder().refreshAfterWrite(10, MINUTES)
			.maximumSize(500)
			.build(buildDataTypeCacheLoader(client));
	}

	private CacheLoader<DataTypeId, DataType> buildDataTypeCacheLoader(HttpRestClient client) {
		return new CacheLoader<DataTypeId, DataType>() {
			@Override
			public DataType load(DataTypeId id) throws Exception {
				return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, asEndpoint(ENTITIES, id), GET, DataType.class);
			}
		};
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
			return NULL_ID;
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
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, SUGGESTIONS, selectorString), GET, responseType, EMPTY_GROUP_LIST);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public DataType getDataType(DataTypeId id) {
		try {
			return dataTypeCache.get(id);
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

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public References getReferences(DataTypeId documentId) {
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, asEndpoint(DOCUMENTS, documentId, REFERENCES), GET, References.class, EMPTY_GROUP_LIST);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Statistics getStatistics(DataTypeId selectorId) {
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, selectorId, STATISTICS), GET, Statistics.class, EMPTY_GROUP_LIST);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public DocumentInfos lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request) {
		return client.loadUrlAsObject(HBASE_INDEXING_SERVICE, asEndpoint(SELECTORS, request.getSelectorId(), INVERTED_INDEX), GET, DocumentInfos.class,
				request.getGroups());
	}
}