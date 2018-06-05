package net.thomas.portfolio.hbase_index.service;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.DOCUMENTS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SAMPLES_PATH;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.thomas.portfolio.hbase_index.fake.FakeDataSetGenerator;
import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RestController
@RequestMapping(value = DOCUMENTS_PATH + "/{dti_type}")
public class DocumentController {
	private FakeHbaseIndex index;

	@Lookup
	public FakeDataSetGenerator getGenerator() {
		return null;
	}

	@PostConstruct
	public void setupGenerator() {
		final FakeDataSetGenerator generator = getGenerator();
		index = generator.getSampleDataSet();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = SAMPLES_PATH, method = GET)
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
	@RequestMapping(path = "/{dti_uid}" + REFERENCES_PATH, method = GET)
	public ResponseEntity<?> getDocumentReferences(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final Collection<Reference> references = index.getReferences(id);
		if (references.size() > 0) {
			return ok(references);
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = "/{dti_uid}", method = GET)
	public ResponseEntity<?> getDocument(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final DataType entity = index.getDataType(id);
		if (entity != null) {
			return ok(entity);
		} else {
			return notFound().build();
		}
	}
}