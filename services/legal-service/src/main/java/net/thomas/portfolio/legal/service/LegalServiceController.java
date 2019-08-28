package net.thomas.portfolio.legal.service;

import static java.lang.System.currentTimeMillis;
import static java.util.Collections.reverse;
import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOGGING_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_UPDATED;
import static net.thomas.portfolio.globals.LegalServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_MESSAGE_PREFIX;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_RULES_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.STATISTICS_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.MESSAGE_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.hateoas.Link.REL_FIRST;
import static org.springframework.hateoas.Link.REL_LAST;
import static org.springframework.hateoas.Link.REL_NEXT;
import static org.springframework.hateoas.Link.REL_PREVIOUS;
import static org.springframework.hateoas.Link.REL_SELF;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.thomas.portfolio.common.services.parameters.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.hateoas.LegalLinkFactory;
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
import net.thomas.portfolio.shared_objects.legal.HistoryItem;
import net.thomas.portfolio.shared_objects.legal.HistoryItemList;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

@RestController
@Api(value = "", description = "Interaction with the legal service")
@EnableConfigurationProperties
public class LegalServiceController {
	private static final Logger LOG = getLogger(LegalServiceController.class);
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final LegalServiceConfiguration config;

	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private SimpMessagingTemplate webSocket;
	@Autowired
	private AnalyticsAdaptor analyticsAdaptor;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private AuditLoggingControl auditLogging;
	private LegalRulesControl legalRules;
	private LegalLinkFactory linkFactory;

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
		webSocket.setMessageConverter(new MappingJackson2MessageConverter());
		legalRules = new LegalRulesControl();
		legalRules.setAnalyticsAdaptor(analyticsAdaptor);
		linkFactory = new LegalLinkFactory(globalUrlPrefix);
		new Thread(() -> {
			LOG.info("Initializing adaptors and validators");
			((HttpRestClientInitializable) analyticsAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getAnalytics()));
			((HttpRestClientInitializable) hbaseAdaptor).initialize(new HttpRestClient(discoveryClient, restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getSelectorTypes());
			LOG.info("Done initializing adaptors and validators");
			LOG.info("Adding fake audit log data");
			auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "AA"), new LegalInformation("me", "For reasons", 0l, Long.MAX_VALUE));
			auditLogging.logStatisticsLookup(new DataTypeId("Type1", "AB"), new LegalInformation("me", "For reasons", currentTimeMillis(), Long.MAX_VALUE));
			auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "FF"), new LegalInformation("me2", "For other reasons", 0l, currentTimeMillis()));
			auditLogging.logInvertedIndexLookup(new DataTypeId("Type2", "01"), new LegalInformation("me3", null, null, null));
			LOG.info("Done adding fake audit log data");
		}).start();
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Verify that looking up the specified selector in inverted index is legal based on the specified legal information", response = Legality.class)
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + INVERTED_INDEX_PATH + LEGAL_RULES_PATH, method = GET)
	public ResponseEntity<?> checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final Legality response = legalRules.checkLegalityOfInvertedIndexLookup(selectorId, legalInfo);
			return ok(response);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Verify that looking up statistics for the specified selector is legal based on the specified legal information", response = Legality.class)
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + STATISTICS_PATH + LEGAL_RULES_PATH, method = GET)
	public ResponseEntity<?> checkLegalityOfStatisticsLookup(DataTypeId dataTypeId, LegalInformation legalInfo) {
		if (TYPE.isValid(dataTypeId.type) && UID.isValid(dataTypeId.uid)) {
			final Legality response = legalRules.checkLegalityOfStatisticsLookup(dataTypeId, legalInfo);
			return ok(response);
		} else {
			return badRequest().body(TYPE.getReason(dataTypeId.type) + "<BR>" + UID.getReason(dataTypeId.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Audit log that a lookup in inverted index is being executed, justified by the specified legal information (returns true, when log has been written to disk)", response = Boolean.class)
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + INVERTED_INDEX_PATH + AUDIT_LOGGING_PATH, method = POST)
	public ResponseEntity<?> auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final boolean accepted = auditLogging.logInvertedIndexLookup(selectorId, legalInfo);
			webSocket.convertAndSend(MESSAGE_PREFIX + LEGAL_MESSAGE_PREFIX + HISTORY_UPDATED, "updated");
			return ok(accepted);
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Audit log that a lookup in selector statistics is being executed, justified by the specified legal information (returns true, when log has been written to disk)", response = Boolean.class)
	@RequestMapping(path = LEGAL_ROOT_PATH + "/{dti_type}/{dti_uid}" + STATISTICS_PATH + AUDIT_LOGGING_PATH, method = POST)
	public ResponseEntity<?> auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final boolean accepted = auditLogging.logStatisticsLookup(selectorId, legalInfo);
			webSocket.convertAndSend(MESSAGE_PREFIX + LEGAL_MESSAGE_PREFIX + HISTORY_UPDATED, "updated");
			return ResponseEntity.created(null).build();
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch all previous audit logs from history", response = HistoryItemList.class)
	@RequestMapping(path = LEGAL_ROOT_PATH + HISTORY_PATH, method = GET)
	public ResponseEntity<?> lookupAuditLoggingHistory() {
		final HistoryItemList history = new HistoryItemList();
		auditLogging.getAll().forEach((item) -> {
			// TOOD[Thomas]: Create DTOs with links instead
			updateChildLinks(item);
			history.add(item);
		});
		reverse(history);
		history.add(new Link(linkFactory.getHistoryLink(), REL_SELF));
		return ok(history);
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch audit log item from history", response = HistoryItem.class)
	@RequestMapping(path = LEGAL_ROOT_PATH + HISTORY_PATH + "/{itemId}", method = GET)
	public ResponseEntity<?> lookupAuditLoggingHistoryItem(@PathVariable Integer itemId) {
		final HistoryItem item = auditLogging.getItem(itemId);
		if (item != null) {
			// TOOD[Thomas]: Create DTOs with links instead
			updateRootLinks(item);
			return ok(item);
		} else {
			return notFound().build();
		}
	}

	private void updateRootLinks(final HistoryItem item) {
		item.removeLinks();
		addSelfLink(item);
		addNeighbourLinks(item);
		addBorderLinks(item);
	}

	private void updateChildLinks(final HistoryItem item) {
		item.removeLinks();
		addSelfLink(item);
	}

	private void addSelfLink(final HistoryItem item) {
		item.add(buildLink(item.getHI_ItemId(), REL_SELF));
	}

	private void addNeighbourLinks(final HistoryItem item) {
		if (item.getHI_ItemId() > 0) {
			item.add(buildLink(item.getHI_ItemId() - 1, REL_PREVIOUS));
		}
		if (item.getHI_ItemId() < auditLogging.getLastId()) {
			item.add(buildLink(item.getHI_ItemId() + 1, REL_NEXT));
		}
	}

	private void addBorderLinks(final HistoryItem item) {
		if (auditLogging.getLastId() > -1) {
			item.add(buildLink(0, REL_FIRST));
			item.add(buildLink(auditLogging.getLastId(), REL_LAST));
		}
	}

	private Link buildLink(int itemId, final String relation) {
		return new Link(linkFactory.getHistoryItemLink(itemId), relation);
	}
}