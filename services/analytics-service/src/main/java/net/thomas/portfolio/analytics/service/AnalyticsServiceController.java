package net.thomas.portfolio.analytics.service;

import static net.thomas.portfolio.entities.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.entities.ServiceGlobals.ANALYTICS_SERVICE_PATH;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_PRIOR_KNOWLEDGE_PATH;
import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.KNOWN;
import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.PARTIALLY_KNOWN;
import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.UNKNOWN;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.analytics.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.analytics.RecognitionLevel;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;

@Controller
@RequestMapping(ANALYTICS_SERVICE_PATH)
public class AnalyticsServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final AnalyticsServiceConfiguration configuration;
	@Autowired
	private EurekaClient discoveryClient;

	public AnalyticsServiceController(AnalyticsServiceConfiguration configuration) {
		this.configuration = configuration;
	}

	@PostConstruct
	public void prepareForRendering() {
		new Thread(() -> {
			final HttpRestClient hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing());
			final Set<String> dataTypes = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class)
				.getDataTypes();
			TYPE.setValidStrings(dataTypes);
		}).run();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(LOOKUP_PRIOR_KNOWLEDGE_PATH)
	public ResponseEntity<?> lookupPriorKnowledge(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final RecognitionLevel recognition = determineFakeRecognizedValue(id);
			final RecognitionLevel isDanish = determineFakeIsDanishValue(id);
			final String alias = determineFakeAlias(id, recognition);
			return ok(new PreviousKnowledge(alias, recognition, isDanish));
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	private RecognitionLevel determineFakeRecognizedValue(DataTypeId id) {
		final char firstChar = id.uid.charAt(0);
		RecognitionLevel recognition = UNKNOWN;
		if (firstChar > 'A' && firstChar <= 'C') {
			recognition = PARTIALLY_KNOWN;
		} else if (firstChar > 'C' && firstChar <= 'F') {
			recognition = KNOWN;
		}
		return recognition;
	}

	private RecognitionLevel determineFakeIsDanishValue(DataTypeId id) {
		final char secondChar = id.uid.charAt(1);
		RecognitionLevel isDanish = UNKNOWN;
		if (secondChar > 'A' && secondChar <= 'C') {
			isDanish = PARTIALLY_KNOWN;
		} else if (secondChar > 'C' && secondChar <= 'F') {
			isDanish = KNOWN;
		}
		return isDanish;
	}

	private String determineFakeAlias(DataTypeId id, RecognitionLevel recognition) {
		String alias = null;
		if (recognition == KNOWN) {
			alias = "Target " + id.uid.substring(0, 6);
		}
		return alias;
	}
}