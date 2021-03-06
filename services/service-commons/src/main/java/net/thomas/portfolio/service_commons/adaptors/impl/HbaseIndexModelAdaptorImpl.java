package net.thomas.portfolio.service_commons.adaptors.impl;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
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

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheLoader.InvalidCacheLoadException;
import com.google.common.cache.LoadingCache;

import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.UnauthorizedAccessException;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
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
	public static final String GET_SELECTOR_SUGGESTIONS = "getSelectorSuggestions";
	public static final String GET_SAMPLES = "getSamples";
	public static final String GET_FROM_SIMPLE_REPRESENTATION = "getFromSimpleRepresentation";
	public static final String LOOKUP_ENTITY = "lookupEntity";
	public static final String LOOKUP_DOCUMENT_REFERENCES = "lookupDocumentReferences";
	public static final String LOOKUP_SELECTOR_STATISTICS = "lookupSelectorStatistics";
	public static final String LOOKUP_SELECTOR_IN_INVERTED_INDEX = "lookupSelectorInInvertedIndex";

	private static final Logger LOG = getLogger(HbaseIndexModelAdaptorImpl.class);

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;
	private HbaseIndexSchema schema;
	private LoadingCache<DataTypeId, DataType> entityCache;

	@Override
	public void initialize(PortfolioUrlLibrary urlLibrary, HttpRestClient client) {
		this.urlLibrary = urlLibrary;
		this.client = client;
		while (schema == null) {
			try {
				final ParameterizedTypeReference<Resource<HbaseIndexSchemaImpl>> responseType = new ParameterizedTypeReference<>() {
				};
				final String url = urlLibrary.schema();
				schema = unwrap(client.loadUrlAsObject(url, GET, responseType));
			} catch (final UnauthorizedAccessException cause) {
				LOG.error("Unable to fetch schema due to invalid credentials", cause);
				throw cause;
			} catch (final RuntimeException cause) {
				// We silently retry until we succeed or the program is closed from the outside
			}
		}
		entityCache = newBuilder().refreshAfterWrite(10, MINUTES).maximumSize(200).build(buildEntityCacheLoader(client));
	}

	private CacheLoader<DataTypeId, DataType> buildEntityCacheLoader(HttpRestClient client) {
		return new CacheLoader<>() {
			@Override
			public DataType load(DataTypeId id) throws Exception {
				final ParameterizedTypeReference<Resource<DataType>> responseType = new ParameterizedTypeReference<>() {
				};
				final String url = urlLibrary.entities().lookup(id);
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
	@SentinelResource(value = GET_SELECTOR_SUGGESTIONS)
	public List<Selector> getSelectorSuggestions(String simpleRepresentation) {
		final ParameterizedTypeReference<Resources<Selector>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors().suggestions(simpleRepresentation);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	@SentinelResource(value = GET_SAMPLES)
	public Entities getSamples(String dataType, int amount) {
		final ParameterizedTypeReference<Resources<DataType>> responseType = new ParameterizedTypeReference<>() {
		};
		String url;
		if (isSelector(dataType)) {
			url = urlLibrary.selectors().samples(dataType, amount);
		} else if (isDocument(dataType)) {
			url = urlLibrary.documents().samples(dataType, amount);
		} else {
			url = urlLibrary.entities().samples(dataType, amount);
		}
		final List<DataType> entities = unwrap(client.loadUrlAsObject(url, GET, responseType));
		if (entities != null) {
			return new Entities(entities);
		} else {
			return null;
		}
	}

	@Override
	@SentinelResource(value = GET_FROM_SIMPLE_REPRESENTATION)
	public Selector getFromSimpleRep(String type, String simpleRepresentation) {
		final ParameterizedTypeReference<Resource<Selector>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors().fromSimpleRepresentation(type, simpleRepresentation);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	@SentinelResource(value = LOOKUP_ENTITY)
	public DataType getDataType(DataTypeId id) {
		try {
			return entityCache.get(id);
		} catch (final InvalidCacheLoadException cause) {
			if (cause.getMessage().contains("CacheLoader returned null for key")) {
				return null;
			} else {
				throw new UnknownEntityException("Unable to fetch entity", cause);
			}
		} catch (final ExecutionException cause) {
			LOG.error("Error during entity fetch", cause);
			return null;
		}
	}

	@Override
	@SentinelResource(value = LOOKUP_DOCUMENT_REFERENCES)
	public References getReferences(DataTypeId documentId) {
		final ParameterizedTypeReference<Resource<References>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.documents().references(documentId);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	@SentinelResource(value = LOOKUP_SELECTOR_STATISTICS)
	public Statistics getStatistics(DataTypeId selectorId) {
		final ParameterizedTypeReference<Resource<Statistics>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors().statistics(selectorId);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}

	@Override
	@SentinelResource(value = LOOKUP_SELECTOR_IN_INVERTED_INDEX)
	public DocumentInfos lookupSelectorInInvertedIndex(InvertedIndexLookupRequest request) {
		final ParameterizedTypeReference<Resources<DocumentInfo>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors().invertedIndex(request);
		return new DocumentInfos(unwrap(client.loadUrlAsObject(url, GET, responseType)));
	}

	public static class UnknownEntityException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public UnknownEntityException(String message) {
			super(message);
		}

		public UnknownEntityException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}