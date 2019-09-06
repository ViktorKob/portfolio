package net.thomas.portfolio.hbase_index.service;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.DOCUMENTS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SAMPLES_PATH;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;

@RestController
@Api(value = "", description = "Lookup of documents and their related data")
@RequestMapping(DOCUMENTS_PATH + "/{dti_type}")
public class DocumentController {

	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private HbaseIndex index;

	private PortfolioHateoasWrappingHelper hateoasHelper;

	@PostConstruct
	public void initializeService() {
		hateoasHelper = new PortfolioHateoasWrappingHelper(new UrlFactory(() -> {
			return globalUrlPrefix;
		}, new PortfolioUrlSuffixBuilder()));
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch {amount} random sample documents of type {dti_type} from HBASE", response = Entities.class)
	@RequestMapping(path = SAMPLES_PATH, method = GET)
	public ResponseEntity<?> getSamples(@PathVariable String dti_type, Integer amount) {
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
	@ApiOperation(value = "Fetch all references to the document of type {dti_type} with uid {dti_uid}", response = References.class)
	@RequestMapping(path = "/{dti_uid}" + REFERENCES_PATH, method = GET)
	public ResponseEntity<?> getDocumentReferences(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final References references = index.getReferences(id);
		if (references.hasData()) {
			return ok(hateoasHelper.wrap(references, id));
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch the document of type {dti_type} with uid {dti_uid} with all relevant fields for the type", response = DataType.class)
	@RequestMapping(path = "/{dti_uid}", method = GET)
	public ResponseEntity<?> getDocument(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final DataType entity = index.getDataType(id);
		if (entity != null) {
			return ok(hateoasHelper.wrap(entity));
		} else {
			return notFound().build();
		}
	}
}