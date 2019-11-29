package net.thomas.portfolio.service_commons.adaptors.impl;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SCHEMA;
import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
import static net.thomas.portfolio.services.Service.HBASE_INDEXING_SERVICE;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpMethod.GET;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;

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

// @EnableCircuitBreaker
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
				final ParameterizedTypeReference<Resource<HbaseIndexSchemaImpl>> responseType = new ParameterizedTypeReference<>() {
				};
				final String url = urlFactory.buildUrl(HBASE_INDEXING_SERVICE, SCHEMA);
				schema = unwrap(client.loadUrlAsObject(url, GET, responseType));
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
		return new CacheLoader<>() {
			@Override
			public DataType load(DataTypeId id) throws Exception {
				final ParameterizedTypeReference<Resource<DataType>> responseType = new ParameterizedTypeReference<>() {
				};
				final String url = urlLibrary.entities.lookup(id);
				return unwrap(client.loadUrlAsObject(url, GET, responseType));
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
	// @HystrixCommand(commandProperties = { @HystrixProperty(name =
	// "circuitBreaker.requestVolumeThreshold", value = "3") })
	public List<Selector> getSelectorSuggestions(String simpleRepresentation) {
		final ParameterizedTypeReference<Resources<Selector>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors.suggestions(simpleRepresentation);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	public Entities getSamples(String dataType, int amount) {
		final ParameterizedTypeReference<Resources<DataType>> responseType = new ParameterizedTypeReference<>() {
		};
		String url;
		if (isDocument(dataType)) {
			url = urlLibrary.selectors.samples(dataType, amount);
		} else if (isDocument(dataType)) {
			url = urlLibrary.documents.samples(dataType, amount);
		} else {
			url = urlLibrary.entities.samples(dataType, amount);
		}
		final List<DataType> entities = unwrap(client.loadUrlAsObject(url, GET, responseType));
		if (entities != null) {
			return new Entities(entities);
		} else {
			return null;
		}
	}

	@Override
	// @HystrixCommand(commandProperties = { @HystrixProperty(name =
	// "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Selector getFromSimpleRep(String type, String simpleRepresentation) {
		final ParameterizedTypeReference<Resource<Selector>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors.simpleRepresentation(type, simpleRepresentation);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	// @HystrixCommand(commandProperties = { @HystrixProperty(name =
	// "circuitBreaker.requestVolumeThreshold", value = "3") })
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
	// @HystrixCommand(commandProperties = { @HystrixProperty(name =
	// "circuitBreaker.requestVolumeThreshold", value = "3") })
	public References getReferences(DataTypeId documentId) {
		final ParameterizedTypeReference<Resource<References>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.documents.references(documentId);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	// @HystrixCommand(commandProperties = { @HystrixProperty(name =
	// "circuitBreaker.requestVolumeThreshold", value = "3") })
	public Statistics getStatistics(DataTypeId selectorId) {
		final ParameterizedTypeReference<Resource<Statistics>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors.statistics(selectorId);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	// @HystrixCommand(commandProperties = { @HystrixProperty(name =
	// "circuitBreaker.requestVolumeThreshold", value = "3") })
	public DocumentInfos lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request) {
		final ParameterizedTypeReference<Resources<DocumentInfo>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors.invertedIndex(request.getSelectorId(), request.getGroups());
		return new DocumentInfos(unwrap(client.loadUrlAsObject(url, GET, responseType)));
	}
}