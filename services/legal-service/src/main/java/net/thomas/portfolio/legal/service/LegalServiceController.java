package net.thomas.portfolio.legal.service;

import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOGGING_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_RULES_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.STATISTICS_PATH;
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

import net.thomas.portfolio.common.services.parameters.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.legal.system.AuditLoggingControl;
import net.thomas.portfolio.legal.system.LegalRulesControl;
import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.service_commons.validation.UidValidator;
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
	private LegalRulesControl legalRulesSystem;
	private AuditLoggingControl auditLoggingSystem;

	public LegalServiceController(LegalServiceConfiguration config) {
		this.config = config;
	}

	@Bean(name = "AnalyticsAdaptor")
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
		new Thread(() -> {
			((HttpRestClientInitializable) analyticsAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getAnalytics()));
			((HttpRestClientInitializable) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getSelectorTypes());
		}).start();
		legalRulesSystem = new LegalRulesControl();
		legalRulesSystem.setAnalyticsAdaptor(analyticsAdaptor);
		auditLoggingSystem = new AuditLoggingControl();
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + INVERTED_INDEX_PATH + LEGAL_RULES_PATH, method = GET)
	public ResponseEntity<?> checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final Legality response = legalRulesSystem.checkLegalityOfInvertedIndexLookup(selectorId, legalInfo);
			return ok(response);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + STATISTICS_PATH + LEGAL_RULES_PATH, method = GET)
	public ResponseEntity<?> checkLegalityOfStatisticsLookup(DataTypeId dataTypeId, LegalInformation legalInfo) {
		if (TYPE.isValid(dataTypeId.type) && UID.isValid(dataTypeId.uid)) {
			final Legality response = legalRulesSystem.checkLegalityOfStatisticsLookup(dataTypeId, legalInfo);
			return ok(response);
		} else {
			return badRequest().body(TYPE.getReason(dataTypeId.type) + "<BR>" + UID.getReason(dataTypeId.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + INVERTED_INDEX_PATH + AUDIT_LOGGING_PATH, method = POST)
	public ResponseEntity<?> auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final boolean accepted = auditLoggingSystem.logInvertedIndexLookup(selectorId, legalInfo);
			return ok(accepted);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + STATISTICS_PATH + AUDIT_LOGGING_PATH, method = POST)
	public ResponseEntity<?> auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final boolean accepted = auditLoggingSystem.logInvertedIndexLookup(selectorId, legalInfo);
			return ok(accepted);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}
}