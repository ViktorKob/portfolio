package net.thomas.portfolio.hbase_index.service;

import static java.lang.Integer.MAX_VALUE;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_DATA_TYPE_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_STATISTICS_PATH;
import static net.thomas.portfolio.globals.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.util.HashSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.thomas.portfolio.common.services.validation.IntegerRangeValidator;
import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.hbase_index.fake.FakeDataSetGenerator;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

@Controller
@RequestMapping(HBASE_INDEXING_SERVICE_PATH)
public class HbaseIndexingServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("type", true);
	private static final SpecificStringPresenceValidator DOCUMENT_TYPE = new SpecificStringPresenceValidator("type", true);
	private static final SpecificStringPresenceValidator SELECTOR_TYPE = new SpecificStringPresenceValidator("type", true);
	private static final UidValidator UID = new UidValidator("uid", true);
	private static final IntegerRangeValidator AMOUNT = new IntegerRangeValidator("amount", 1, MAX_VALUE, true);

	private final HbaseIndexingServiceConfiguration config;
	private HbaseIndexSchema schema;
	private FakeHbaseIndex index;

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
		TYPE.setValidStrings(new HashSet<>(schema.getDataTypes()));
		DOCUMENT_TYPE.setValidStrings(new HashSet<>(schema.getDocumentTypes()));
		SELECTOR_TYPE.setValidStrings(new HashSet<>(schema.getSelectorTypes()));
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_SCHEMA_PATH)
	public ResponseEntity<?> getSchema() {
		return ok(schema);
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_SAMPLES_PATH)
	public ResponseEntity<?> getSamples(String type, Integer amount) {
		if (TYPE.isValid(type) && AMOUNT.isValid(amount)) {
			return ok(index.getSamples(type, amount));
		} else {
			return badRequest().body(TYPE.getReason(type) + "<BR>" + AMOUNT.getReason(amount));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_DATA_TYPE_PATH)
	public ResponseEntity<?> getDatatype(String type, String uid) {
		if (TYPE.isValid(type) && UID.isValid(uid)) {
			return ResponseEntity.ok(index.getDataType(type, uid));
		} else {
			return badRequest().body(TYPE.getReason(type) + "<BR>" + UID.getReason(uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_REFERENCES_PATH)
	public ResponseEntity<?> getReferences(String type, String uid) {
		if (DOCUMENT_TYPE.isValid(type) && UID.isValid(uid)) {
			return ResponseEntity.ok(index.getReferences(uid));
		} else {
			return badRequest().body(DOCUMENT_TYPE.getReason(type) + "<BR>" + UID.getReason(uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_STATISTICS_PATH)
	public ResponseEntity<?> getStatistics(String type, String uid) {
		if (SELECTOR_TYPE.isValid(type) && UID.isValid(uid)) {
			return ResponseEntity.ok(index.getStatistics(uid));
		} else {
			return badRequest().body(SELECTOR_TYPE.getReason(type) + "<BR>" + UID.getReason(uid));
		}
	}
}