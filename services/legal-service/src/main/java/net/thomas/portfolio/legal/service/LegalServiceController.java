package net.thomas.portfolio.legal.service;

import static net.thomas.portfolio.entities.Service.ANALYTICS_SERVICE;
import static net.thomas.portfolio.entities.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.entities.ServiceGlobals.LEGAL_SERVICE_PATH;
import static net.thomas.portfolio.enums.AnalyticsServiceEndpoint.LOOKUP_PRIOR_KNOWLEDGE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.globals.LegalServiceGlobals.CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP_PATH;
import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.KNOWN;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
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

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.analytics.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@Controller
@RequestMapping(LEGAL_SERVICE_PATH)
public class LegalServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final LegalServiceConfiguration configuration;
	@Autowired
	private EurekaClient discoveryClient;
	private HttpRestClient analyticsClient;

	public LegalServiceController(LegalServiceConfiguration configuration) {
		this.configuration = configuration;
	}

	@PostConstruct
	public void prepareForRendering() {
		analyticsClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getAnalytics());
		final HttpRestClient hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), configuration.getHbaseIndexing());
		final Set<String> dataTypes = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class)
			.getDataTypes();
		TYPE.setValidStrings(dataTypes);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP_PATH)
	public ResponseEntity<?> checkLegalityOfInvertedIndexLookup(DataTypeId id, LegalInformation legalInfo) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final PreviousKnowledge knowledge = analyticsClient.loadUrlAsObject(ANALYTICS_SERVICE, LOOKUP_PRIOR_KNOWLEDGE, PreviousKnowledge.class,
					new PreSerializedParameter("dti_type", id.type), new PreSerializedParameter("dti_uid", id.uid));
			if (knowledge.isDanish == KNOWN && (legalInfo.justification == null || legalInfo.justification.isEmpty())) {
				return ok(ILLEGAL);
			} else {
				return ok(LEGAL);
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}