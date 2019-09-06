package net.thomas.portfolio.hbase_index.service;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.FROM_SIMPLE_REP_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTORS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.STATISTICS_PATH;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookup;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookupBuilder;
import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@RestController
@Api(value = "", description = "Lookup of selectors and their related data")
@RequestMapping(value = SELECTORS_PATH + "/{dti_type}", produces = "application/hal+json")
public class SelectorController {
	private final ExecutorService lookupExecutor;

	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private HbaseIndexSchema schema;
	@Autowired
	private HbaseIndex index;
	@Autowired
	private SimpleRepresentationParserLibrary parserLibrary;

	private PortfolioHateoasWrappingHelper hateoasHelper;

	public SelectorController() {
		lookupExecutor = newSingleThreadExecutor();
	}

	@PostConstruct
	public void initializeService() {
		hateoasHelper = new PortfolioHateoasWrappingHelper(new UrlFactory(() -> {
			return globalUrlPrefix;
		}, new PortfolioUrlSuffixBuilder()));
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Lookup selector using it's simple string representation (will return the ID for the selector even if it does not exist)", response = Selector.class)
	@RequestMapping(path = FROM_SIMPLE_REP_PATH + "/{simpleRepresentation}/", method = GET)
	public ResponseEntity<?> getEntityId(@PathVariable String dti_type, @PathVariable String simpleRepresentation) {
		final Selector selector = parserLibrary.parse(dti_type, simpleRepresentation);
		if (selector != null) {
			return ok(hateoasHelper.wrap(selector));
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch {amount} random sample selectors of type {dti_type} from HBASE", response = Entities.class)
	@RequestMapping(path = SAMPLES_PATH, method = GET)
	public ResponseEntity<?> getSamples(@ApiParam(value = "Type of the selector to fetch") @PathVariable String dti_type,
			@ApiParam(value = "Maximum number of samples to fetch", defaultValue = "10") Integer amount) {
		if (amount == null) {
			amount = 10;
		}
		final Entities samples = index.getSamples(dti_type, amount);
		if (samples != null && samples.hasData()) {
			return ok(hateoasHelper.wrap(samples, dti_type, amount));
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Lookup statistics for selector with type {dti_type} and uid {dti_uid}", response = Statistics.class)
	@RequestMapping(path = "/{dti_uid}" + STATISTICS_PATH, method = GET)
	public ResponseEntity<?> getStatistics(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final Statistics statistics = index.getStatistics(id);
		if (statistics.hasData()) {
			return ok(hateoasHelper.wrap(statistics, id));
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Lookup selector with type {dti_type} and uid {dti_uid}", response = DataType.class)
	@RequestMapping(path = "/{dti_uid}", method = GET)
	public ResponseEntity<?> getSelector(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final DataType entity = index.getDataType(id);
		if (entity != null) {
			return ok(hateoasHelper.wrap(entity));
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Lookup selector with type {dti_type} and uid {dti_uid} in inverted index using the specified constraints", response = DocumentInfos.class)
	@RequestMapping(path = "/{dti_uid}" + INVERTED_INDEX_PATH, method = GET)
	public ResponseEntity<?> lookupSelectorInInvertedIndex(@PathVariable String dti_type, @PathVariable String dti_uid, LegalInformation legalInfo,
			Bounds bounds, @RequestParam(value = "documentType", required = false) HashSet<String> documentTypes,
			@RequestParam(value = "relation", required = false) HashSet<String> relations) {
		final DataTypeId selectorId = new DataTypeId(dti_type, dti_uid);
		final DocumentInfos results = buildLookup(selectorId, bounds, documentTypes, relations).execute();
		return ok(hateoasHelper.wrap(results, selectorId));
	}

	private InvertedIndexLookup buildLookup(DataTypeId selectorId, Bounds bounds, Set<String> documentTypes, Set<String> relations) {
		final InvertedIndexLookupBuilder builder = new InvertedIndexLookupBuilder(index, lookupExecutor);
		builder.setSelectorId(selectorId);
		builder.updateBounds(bounds);
		builder.setIndexables(schema.getIndexables(selectorId.type));
		if (documentTypes != null) {
			builder.addIndexableFilter(new IndexableFilter.DataTypeFilter(documentTypes));
		}
		if (relations != null) {
			builder.addIndexableFilter(new IndexableFilter.RelationFilter(relations));
		}
		return builder.build();
	}
}