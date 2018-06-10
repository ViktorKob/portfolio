package net.thomas.portfolio.hbase_index.service;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTORS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.STATISTICS_PATH;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.hbase_index.fake.FakeDataSetGenerator;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndexSchemaImpl;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookup;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookupBuilder;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.services.LegalAdaptorImpl;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@RestController
@RequestMapping(value = SELECTORS_PATH + "/{dti_type}")
public class SelectorController {
	private FakeHbaseIndex index;
	private ExecutorService lookupExecutor;
	private LegalAdaptorImpl legalAdaptor;

	@Autowired
	private EurekaClient discoveryClient;
	private final HbaseIndexingServiceConfiguration config;
	private FakeHbaseIndexSchemaImpl schema;

	@Autowired
	public SelectorController(HbaseIndexingServiceConfiguration config) {
		this.config = config;
	}

	@Lookup
	public FakeDataSetGenerator getGenerator() {
		return null;
	}

	@PostConstruct
	public void setupGenerator() {
		final FakeDataSetGenerator generator = getGenerator();
		index = generator.getSampleDataSet();
		schema = generator.getSchema();
		lookupExecutor = newSingleThreadExecutor();
		new Thread(() -> {
			legalAdaptor = new LegalAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getLegal()));
		}).run();
	}

	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = SAMPLES_PATH, method = GET)
	public ResponseEntity<?> getSamples(@PathVariable String dti_type, Integer amount) {
		if (amount == null) {
			amount = 10;
		}
		final Collection<DataType> samples = index.getSamples(dti_type, amount);
		if (samples != null && samples.size() > 0) {
			return ok(samples);
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{dti_uid}" + STATISTICS_PATH, method = GET)
	public ResponseEntity<?> getStatistics(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final Map<StatisticsPeriod, Long> statistics = index.getStatistics(id);
		if (statistics.size() > 0) {
			return ok(statistics);
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{dti_uid}", method = GET)
	public ResponseEntity<?> getSelector(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final DataType entity = index.getDataType(id);
		if (entity != null) {
			return ok(entity);
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{dti_uid}" + INVERTED_INDEX_PATH, method = GET)
	public ResponseEntity<?> lookupSelectorInInvertedIndex(@PathVariable String dti_type, @PathVariable String dti_uid, LegalInformation legalInfo,
			Bounds bounds, @RequestParam(value = "documentType", required = false) HashSet<String> documentTypes,
			@RequestParam(value = "relation", required = false) HashSet<String> relations) {
		final DataTypeId selectorId = new DataTypeId(dti_type, dti_uid);
		if (lookupIsLegal(selectorId, legalInfo)) {
			final List<DocumentInfo> results = buildLookup(selectorId, bounds, documentTypes, relations).execute();
			return ok(results);
		} else {
			return badRequest().body("You must justify your search before being able to continue");
		}
	}

	private boolean lookupIsLegal(DataTypeId selectorId, LegalInformation legalInfo) {
		return LEGAL == legalAdaptor.checkLegalityOfSelectorQuery(selectorId, legalInfo);
	}

	private InvertedIndexLookup buildLookup(DataTypeId selectorId, Bounds bounds, HashSet<String> documentTypes, HashSet<String> relations) {
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
