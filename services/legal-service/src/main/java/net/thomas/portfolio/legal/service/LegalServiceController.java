package net.thomas.portfolio.legal.service;

import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOG_INVERTED_INDEX_LOOKUP_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOG_STATISTICS_LOOKUP_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.CHECK_LEGALITY_OF_QUERY_ON_SELECTOR_PATH;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
import net.thomas.portfolio.legal.system.AuditLoggingControl;
import net.thomas.portfolio.legal.system.AuditingRulesControl;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.services.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

@Controller
public class LegalServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final LegalServiceConfiguration config;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private AnalyticsAdaptor analyticsAdaptor;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	private AuditingRulesControl auditingRulesSystem;
	private AuditLoggingControl auditLoggingSystem;

	public LegalServiceController(LegalServiceConfiguration config) {
		this.config = config;
	}

	@Bean
	public AnalyticsAdaptor getAnalyticsAdaptor() {
		return new AnalyticsAdaptorImpl();
	}

	@Bean
	public HbaseIndexModelAdaptor getHbaseIndexModelAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@PostConstruct
	public void initializeService() {
		((AnalyticsAdaptorImpl) analyticsAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getAnalytics()));
		((HbaseIndexModelAdaptorImpl) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
		new Thread(() -> {
			TYPE.setValidStrings(hbaseAdaptor.getSelectorTypes());
		}).run();
		auditingRulesSystem = new AuditingRulesControl();
		auditingRulesSystem.setAnalyticsAdaptor(analyticsAdaptor);
		auditLoggingSystem = new AuditLoggingControl();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = AUDIT_LOG_INVERTED_INDEX_LOOKUP_PATH, method = POST)
	public ResponseEntity<?> auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final boolean accepted = auditLoggingSystem.logInvertedIndexLookup(selectorId, legalInfo);
			return ok(accepted);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = AUDIT_LOG_STATISTICS_LOOKUP_PATH, method = POST)
	public ResponseEntity<?> auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final boolean accepted = auditLoggingSystem.logInvertedIndexLookup(selectorId, legalInfo);
			return ok(accepted);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = CHECK_LEGALITY_OF_QUERY_ON_SELECTOR_PATH, method = GET)
	public ResponseEntity<?> checkLegalityOfQueryOnSelector(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final Legality response = auditingRulesSystem.checkLegalityOfSelectorQuery(selectorId, legalInfo);
			return ok(response);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}
}