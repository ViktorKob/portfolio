package net.thomas.portfolio.legal.service;

import static net.thomas.portfolio.globals.LegalServiceGlobals.CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;
import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.KNOWN;
import static net.thomas.portfolio.shared_objects.legal.Legality.ILLEGAL;
import static net.thomas.portfolio.shared_objects.legal.Legality.LEGAL;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

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
import net.thomas.portfolio.service_commons.services.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

@Controller
@RequestMapping(LEGAL_SERVICE_PATH)
public class LegalServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final LegalServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	private AnalyticsAdaptorImpl analyticsAdaptor;

	public LegalServiceController(LegalServiceConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	public void prepareForRendering() {
		new Thread(() -> {
			analyticsAdaptor = new AnalyticsAdaptorImpl(new HttpRestClient(discoveryClient, getRestTemplate(), config.getAnalytics()));
			final HbaseIndexModelAdaptor hbaseIndexAdaptor = new HbaseIndexModelAdaptorImpl(
					new HttpRestClient(discoveryClient, getRestTemplate(), config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseIndexAdaptor.getDataTypes());
		}).run();
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
			final PriorKnowledge knowledge = analyticsAdaptor.getPriorKnowledge(id);
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