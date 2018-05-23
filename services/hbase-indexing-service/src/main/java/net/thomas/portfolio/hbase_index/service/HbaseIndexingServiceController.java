package net.thomas.portfolio.hbase_index.service;

import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_DATATYPE_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SCHEMA_PATH;
import static net.thomas.portfolio.globals.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.thomas.portfolio.globals.HbaseIndexingServiceGlobals;
import net.thomas.portfolio.hbase_index.fake.FakeDataSetGenerator;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

@Controller
@RequestMapping(HBASE_INDEXING_SERVICE_PATH)
public class HbaseIndexingServiceController {

	private final HbaseIndexingServiceConfiguration config;
	private HbaseIndexSchema schema;
	private HbaseIndex index;

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
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_SCHEMA_PATH)
	public ResponseEntity<?> getSchema() {
		return ok(schema);
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_SAMPLES_PATH)
	public ResponseEntity<?> fetchUsageActivity(String type, Integer amount) {
		if (isInvalid(type, amount)) {
			return badRequest().body("Both type and amount must be present.<BR>- type must belong to " + render(schema.getDataTypes())
					+ "<BR>- amount must be > 0.<BR>Values: type=" + type + ", amount=" + amount);
		}
		return ok(((FakeHbaseIndex) index).getSamples(type, amount));
	}

	@Secured("ROLE_USER")
	@RequestMapping(GET_DATATYPE_PATH)
	public ResponseEntity<?> getDatatype(String type, String uid) {
		if (isInvalid(type, uid)) {
			return badRequest().body("Both type and uid must be present. The uid must be of even length. Values: type=" + type + ", uid=" + uid);
		}
		return ResponseEntity.ok(index.getDataType(type, uid));
	}

	private String render(Collection<String> elements) {
		return "[ " + elements.stream()
			.collect(joining(", ")) + " ]";
	}

	private boolean isInvalid(String type, Integer amount) {
		return type == null || !schema.getDataTypes()
			.contains(type) || amount < 1;
	}

	private boolean isInvalid(String type, String uid) {
		return type == null || !schema.getDataTypes()
			.contains(type) || uid == null || isOfOddLength(uid);
	}

	private boolean isOfOddLength(String uid) {
		return uid.length() % 2 == 1;
	}

	@Secured("ROLE_USER")
	@RequestMapping(HbaseIndexingServiceGlobals.SELECTOR_LOOKUP_PATH)
	public ResponseEntity<?> fetchUsageActivity(String type, String uid) {
		return badRequest().body("Both type and uid must be present. The uid must be of even length. Values: type=" + type + ", uid=" + uid);
	}
}