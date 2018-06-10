package net.thomas.portfolio.analytics.service;

import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_PRIOR_KNOWLEDGE_PATH;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.POSSIBLY;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.UNLIKELY;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@Controller
public class AnalyticsServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final AnalyticsServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;

	public AnalyticsServiceController(AnalyticsServiceConfiguration config) {
		this.config = config;
	}

	@PostConstruct
	public void prepareForRendering() {
		new Thread(() -> {
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
	@RequestMapping(path = LOOKUP_PRIOR_KNOWLEDGE_PATH, method = GET)
	public ResponseEntity<?> lookupPriorKnowledge(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			final ConfidenceLevel recognition = determineFakeRecognizedValue(id);
			final ConfidenceLevel isRestricted = determineFakeIsRestrictedValue(id);
			final String alias = determineFakeAlias(id, recognition);
			return ok(new PriorKnowledge(alias, recognition, isRestricted));
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}

	private ConfidenceLevel determineFakeRecognizedValue(DataTypeId id) {
		final char firstChar = id.uid.charAt(0);
		ConfidenceLevel recognition = UNLIKELY;
		if (firstChar >= 'A' && firstChar <= 'C') {
			recognition = POSSIBLY;
		} else if (firstChar > 'C' && firstChar <= 'F') {
			recognition = CERTAIN;
		}
		return recognition;
	}

	private ConfidenceLevel determineFakeIsRestrictedValue(DataTypeId id) {
		final char secondChar = id.uid.charAt(1);
		ConfidenceLevel isRestricted = UNLIKELY;
		if (secondChar >= 'A' && secondChar <= 'C') {
			isRestricted = POSSIBLY;
		} else if (secondChar > 'C' && secondChar <= 'F') {
			isRestricted = CERTAIN;
		}
		return isRestricted;
	}

	private String determineFakeAlias(DataTypeId id, ConfidenceLevel recognition) {
		String alias = null;
		if (recognition == CERTAIN) {
			alias = "Target " + id.uid.substring(0, 6);
		}
		return alias;
	}
}