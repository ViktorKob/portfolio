package net.thomas.portfolio.hbase_index.service;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.ENTITIES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTORS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SUGGESTIONS_PATH;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import net.thomas.portfolio.hbase_index.fake.FakeDataSetGenerator;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndexSchemaImpl;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@Controller
public class HbaseIndexingServiceController {

	private final HbaseIndexingServiceConfiguration config;
	private FakeHbaseIndexSchemaImpl schema;
	private FakeHbaseIndex index;

	@Autowired
	public HbaseIndexingServiceController(HbaseIndexingServiceConfiguration config) {
		this.config = config;
	}

	@Lookup
	public FakeDataSetGenerator getGenerator() {
		return null;
	}

	@PostConstruct
	private void setupIndexAccess() {
		final FakeDataSetGenerator generator = getGenerator();
		schema = generator.getSchema();
		generator.buildSampleDataSet(config.getRandomSeed());
		index = generator.getSampleDataSet();
	}

	@Secured("ROLE_USER")
	@RequestMapping(SCHEMA_PATH)
	public ResponseEntity<?> getSchema() {
		return ok(schema);
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = SELECTORS_PATH + SUGGESTIONS_PATH + "/{selectorString}", method = GET)
	public ResponseEntity<?> getSelectorSuggestions(@PathVariable String selectorString) {
		final List<DataTypeId> suggestions = schema.getSelectorSuggestions(selectorString);
		if (suggestions != null && suggestions.size() > 0) {
			return ok(suggestions);
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = ENTITIES_PATH + "/{dti_type}" + SAMPLES_PATH, method = GET)
	public ResponseEntity<?> getDatatype(@PathVariable String dti_type, Integer amount) {
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
	@RequestMapping(path = ENTITIES_PATH + "/{dti_type}/{dti_uid}", method = GET)
	public ResponseEntity<?> getDataType(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final DataType entity = index.getDataType(id);
		if (entity != null) {
			return ok(entity);
		} else {
			return notFound().build();
		}
	}
}