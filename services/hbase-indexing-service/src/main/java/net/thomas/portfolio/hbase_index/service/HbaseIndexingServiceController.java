package net.thomas.portfolio.hbase_index.service;

import static java.lang.Integer.MAX_VALUE;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_DATA_TYPE_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_STATISTICS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.INVERTED_INDEX_LOOKUP_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.validation.IntegerRangeValidator;
import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.hbase_index.fake.FakeDataSetGenerator;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndexSchemaImpl;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookup;
import net.thomas.portfolio.hbase_index.lookup.InvertedIndexLookupBuilder;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.services.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.IndexableFilter;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@Controller
@RequestMapping(HBASE_INDEXING_SERVICE_PATH)
public class HbaseIndexingServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final SpecificStringPresenceValidator DOCUMENT_TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final SpecificStringPresenceValidator SELECTOR_TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);
	private static final IntegerRangeValidator AMOUNT = new IntegerRangeValidator("amount", 1, MAX_VALUE, true);

	private final HbaseIndexingServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	private FakeHbaseIndexSchemaImpl schema;
	private FakeHbaseIndex index;
	private ExecutorService lookupExecutor;
	private LegalAdaptorImpl legalAdaptor;

	@Autowired
	public HbaseIndexingServiceController(HbaseIndexingServiceConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	private void setupIndexAccess() {
		final FakeDataSetGenerator generator = new FakeDataSetGenerator();
		schema = generator.getSchema();
		generator.buildSampleDataSet(config.getRandomSeed());
		index = generator.getSampleDataSet();
		lookupExecutor = Executors.newSingleThreadExecutor();
		TYPE.setValidStrings(new HashSet<>(schema.getDataTypes()));
		DOCUMENT_TYPE.setValidStrings(new HashSet<>(schema.getDocumentTypes()));
		SELECTOR_TYPE.setValidStrings(new HashSet<>(schema.getSelectorTypes()));
		new Thread(() -> {
			legalAdaptor = new LegalAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getLegal()));
		}).run();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_SCHEMA_PATH)
	public ResponseEntity<?> getSchema() {
		return ok(schema);
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_SAMPLES_PATH)
	public ResponseEntity<?> getSamples(String dti_type, Integer amount) {
		if (TYPE.isValid(dti_type) && AMOUNT.isValid(amount)) {
			final Collection<DataType> samples = index.getSamples(dti_type, amount);
			if (samples != null && samples.size() > 0) {
				return ok(samples);
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(dti_type) + "<BR>" + AMOUNT.getReason(amount));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_DATA_TYPE_PATH)
	public ResponseEntity<?> getDatatype(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final DataType entity = index.getDataType(id);
			if (entity != null) {
				return ok(entity);
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(INVERTED_INDEX_LOOKUP_PATH)
	public ResponseEntity<?> invertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo, Bounds bounds,
			@RequestParam(value = "documentType", required = false) HashSet<String> documentTypes,
			@RequestParam(value = "relation", required = false) HashSet<String> relations) {
		if (SELECTOR_TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			if (lookupIsLegal(selectorId, legalInfo)) {
				final List<DocumentInfo> results = buildLookup(selectorId, bounds, documentTypes, relations).execute();
				return ok(results);
			} else {
				return badRequest().body("You must justify your search before being able to continue");
			}
		} else {
			return badRequest().body(SELECTOR_TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	private boolean lookupIsLegal(DataTypeId selectorId, LegalInformation legalInfo) {
		return LEGAL == legalAdaptor.checkLegalityOfInvertedIndexLookup(selectorId, legalInfo);
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

	@Secured("ROLE_USER")
	@RequestMapping(GET_REFERENCES_PATH)
	public ResponseEntity<?> getReferences(DataTypeId documentId) {
		if (DOCUMENT_TYPE.isValid(documentId.type) && UID.isValid(documentId.uid)) {
			final Collection<Reference> references = index.getReferences(documentId);
			if (references != null && references.size() > 0) {
				return ok(references);
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(DOCUMENT_TYPE.getReason(documentId.type) + "<BR>" + UID.getReason(documentId.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_STATISTICS_PATH)
	public ResponseEntity<?> getStatistics(DataTypeId selectorId) {
		if (SELECTOR_TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final Map<StatisticsPeriod, Long> references = index.getStatistics(selectorId.uid);
			if (references != null) {
				return ok(references);
			} else {
				return notFound().build();
			}
		} else {
			return badRequest().body(SELECTOR_TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}
}