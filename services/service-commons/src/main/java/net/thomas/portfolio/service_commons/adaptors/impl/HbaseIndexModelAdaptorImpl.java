package net.thomas.portfolio.service_commons.adaptors.impl;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SCHEMA;
import static net.thomas.portfolio.services.Service.HBASE_INDEXING_SERVICE;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpMethod.GET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.UnauthorizedAccessException;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

@EnableCircuitBreaker
public class HbaseIndexModelAdaptorImpl implements PortfolioInfrastructureAware, HbaseIndexModelAdaptor {
	private static final Logger LOG = getLogger(HbaseIndexModelAdaptorImpl.class);

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;
	private HbaseIndexSchema schema;
	private LoadingCache<DataTypeId, DataType> dataTypeCache;

	@Override
	public void initialize(final UrlFactory urlFactory, final HttpRestClient client) {
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
		this.client = client;
		while (schema == null) {
			try {
				final String url = urlFactory.buildUrl(HBASE_INDEXING_SERVICE, SCHEMA);
				schema = client.loadUrlAsObject(url, GET, HbaseIndexSchemaImpl.class);
			} catch (final UnauthorizedAccessException e) {
				LOG.error("Unable to fetch schema due to invalid credentials", e);
				throw e;
			} catch (final RuntimeException e) {
				// We try again until we succeed or the service is closed from the outside
			}
		}
		dataTypeCache = newBuilder().refreshAfterWrite(10, MINUTES).maximumSize(200).build(buildDataTypeCacheLoader(client));
	}

	private CacheLoader<DataTypeId, DataType> buildDataTypeCacheLoader(HttpRestClient client) {
		return new CacheLoader<DataTypeId, DataType>() {
			@Override
			public DataType load(DataTypeId id) throws Exception {
				// final ParameterizedTypeReference<Resource<DataType>> responseType = new
				// ParameterizedTypeReference<Resource<DataType>>() {
				// };
				// final Resource<DataType> entity = client.loadUrlAsObject(HBASE_INDEXING_SERVICE,
				// asEndpoint(ENTITIES, id), GET, responseType, EMPTY_GROUP_LIST);
				// return entity.getContent();
				final String url = urlLibrary.hbase.entities.lookup(id);
				final DataType entity = client.loadUrlAsObject(url, GET, DataType.class);
				return entity;
			}
		};
	}

	@Override
	public boolean isSimpleRepresentable(String dataType) {
		return schema.getSimpleRepresentableTypes().contains(dataType);
	}

	@Override
	public boolean isSelector(String dataType) {
		return schema.getSelectorTypes().contains(dataType);
	}

	@Override
	public boolean isDocument(String dataType) {
		return schema.getDocumentTypes().contains(dataType);
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
	public List<Selector> getSelectorSuggestions(String simpleRepresentation) {
		final ParameterizedTypeReference<Resources<Selector>> responseType = new ParameterizedTypeReference<Resources<Selector>>() {
		};
		final String url = urlLibrary.hbase.selectors.suggestions(simpleRepresentation);
		final Resources<Selector> response = client.loadUrlAsObject(url, GET, responseType);
		if (response != null) {
			return new ArrayList<>(response.getContent());
		} else {
			return null;
		}
	}

	@Override
	public Entities getSamples(String dataType, int amount) {
		final ParameterizedTypeReference<Resources<DataType>> responseType = new ParameterizedTypeReference<Resources<DataType>>() {
		};
		final String url = urlLibrary.hbase.entities.samples(dataType, amount);
		final Resources<DataType> response = client.loadUrlAsObject(url, GET, responseType);
		if (response != null) {
			return new Entities(response.getContent());
		} else {
			return null;
		}
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Selector getFromSimpleRep(String type, String simpleRepresentation) {
		final ParameterizedTypeReference<Resource<Selector>> responseType = new ParameterizedTypeReference<Resource<Selector>>() {
		};
		final String url = urlLibrary.hbase.selectors.simpleRepresentation(type, simpleRepresentation);
		final Resource<Selector> response = client.loadUrlAsObject(url, GET, responseType);
		if (response != null) {
			return response.getContent();
		} else {
			return null;
		}
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public DataType getDataType(DataTypeId id) {
		try {
			return dataTypeCache.get(id);
		} catch (final InvalidCacheLoadException e) {
			if (e.getMessage().contains("CacheLoader returned null for key")) {
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
		final String url = urlLibrary.hbase.documents.references(documentId);
		return client.loadUrlAsObject(url, GET, References.class);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Statistics getStatistics(DataTypeId selectorId) {
		final String url = urlLibrary.hbase.selectors.statistics(selectorId);
		return client.loadUrlAsObject(url, GET, Statistics.class);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public DocumentInfos lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request) {
		final ParameterizedTypeReference<Resources<DocumentInfo>> responseType = new ParameterizedTypeReference<Resources<DocumentInfo>>() {
		};
		final String url = urlLibrary.hbase.selectors.invertedIndex(request.getSelectorId(), request.getGroups());
		final Resources<DocumentInfo> response = client.loadUrlAsObject(url, GET, responseType);
		return new DocumentInfos(response.getContent());
	}
}