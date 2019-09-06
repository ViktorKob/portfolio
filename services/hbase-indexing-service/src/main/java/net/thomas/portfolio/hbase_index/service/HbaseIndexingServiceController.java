package net.thomas.portfolio.hbase_index.service;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.ENTITIES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTORS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SUGGESTIONS_PATH;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

@Controller
@Api(value = "", description = "Schema and general data lookup")
@RequestMapping(produces = "application/hal+json")
@EnableConfigurationProperties
public class HbaseIndexingServiceController {

	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private HbaseIndexSchema schema;
	@Autowired
	private SimpleRepresentationParserLibrary parserLibrary;
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
	@ApiOperation(value = "Fetch the schema for the underlying data model", response = HbaseIndexSchemaImpl.class)
	@RequestMapping(path = SCHEMA_PATH, method = GET)
	public ResponseEntity<?> getSchema() {
		return ok(schema);
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Suggest valid selectors based on their simple string representation for types that have one", response = Selectors.class)
	@RequestMapping(path = SELECTORS_PATH + SUGGESTIONS_PATH + "/{simpleRepresentation}/", method = GET)
	public ResponseEntity<?> getSelectorSuggestions(@PathVariable String simpleRepresentation) {
		final List<Selector> suggestions = parserLibrary.getSelectorSuggestions(simpleRepresentation);
		if (suggestions != null && suggestions.size() > 0) {
			return ok(hateoasHelper.wrap(simpleRepresentation, suggestions));
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch {amount} random sample entities of type {dti_type} from HBASE", response = Entities.class)
	@RequestMapping(path = ENTITIES_PATH + "/{dti_type}" + SAMPLES_PATH, method = GET)
	public ResponseEntity<?> getSamples(@PathVariable String dti_type, Integer amount) {
		if (amount == null) {
			amount = 10;
		}
		final Entities samples = index.getSamples(dti_type, amount);
		if (samples != null && samples.hasData()) {
			return ok(hateoasHelper.wrap(dti_type, amount, samples));
		} else {
			return notFound().build();
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch the entity of type {dti_type} with uid {dti_uid} with all relevant fields for the type", response = DataType.class)
	@RequestMapping(path = ENTITIES_PATH + "/{dti_type}/{dti_uid}", method = GET)
	public ResponseEntity<?> getDataType(@PathVariable String dti_type, @PathVariable String dti_uid) {
		final DataTypeId id = new DataTypeId(dti_type, dti_uid);
		final DataType entity = index.getDataType(id);
		if (entity != null) {
			return ok(hateoasHelper.wrap(entity));
		} else {
			return notFound().build();
		}
	}

	/***
	 * Only present for documentation purposes
	 */
	private static class Selectors extends LinkedList<Selector> {
		private static final long serialVersionUID = 1L;
	}
}