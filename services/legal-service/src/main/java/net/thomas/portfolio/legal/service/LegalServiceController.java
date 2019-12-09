package net.thomas.portfolio.legal.service;

import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOGGING_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_UPDATED;
import static net.thomas.portfolio.globals.LegalServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_MESSAGE_PREFIX;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_RULES_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.STATISTICS_PATH;
import static net.thomas.portfolio.service_commons.network.urls.UrlFactory.usingPortfolio;
import static net.thomas.portfolio.services.ServiceGlobals.MESSAGE_PREFIX;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.hateoas.Link.REL_SELF;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.net.URI;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceSupport;
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
import net.thomas.portfolio.legal.system.AuditLoggingControl;
import net.thomas.portfolio.legal.system.LegalRulesControl;
import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.HistoryItem;
import net.thomas.portfolio.shared_objects.legal.HistoryItemList;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;

@RestController
@Api(value = "", description = "Interaction with the legal service")
@EnableConfigurationProperties
@RequestMapping(value = LEGAL_ROOT_PATH, produces = "application/hal+json")
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
	private PortfolioHateoasWrappingHelper hateoasHelper;
	private PortfolioUrlLibrary urlLibrary;
	private final HistoryItemResourceAssembler historyItemResourceAssembler;

	public LegalServiceController(LegalServiceConfiguration config) {
		this.config = config;
		historyItemResourceAssembler = new HistoryItemResourceAssembler();
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
		final UrlFactory urlFactory = new UrlFactory(() -> {
			return globalUrlPrefix;
		}, new PortfolioUrlSuffixBuilder());
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
		hateoasHelper = new PortfolioHateoasWrappingHelper(urlFactory);
		new Thread(() -> {
			LOG.info("Initializing adaptors and validators");
			((PortfolioInfrastructureAware) analyticsAdaptor).initialize(new PortfolioUrlLibrary(usingPortfolio(discoveryClient, config.getAnalytics())),
					new HttpRestClient(restTemplate, config.getAnalytics()));
			((PortfolioInfrastructureAware) hbaseAdaptor).initialize(new PortfolioUrlLibrary(usingPortfolio(discoveryClient, config.getHbaseIndexing())),
					new HttpRestClient(restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getSelectorTypes());
			LOG.info("Done initializing adaptors and validators");
			// LOG.info("Adding fake audit log data");
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "AA"), new LegalInformation("me",
			// "For reasons", 0l, Long.MAX_VALUE));
			// auditLogging.logStatisticsLookup(new DataTypeId("Type1", "AB"), new LegalInformation("me", "For
			// reasons", currentTimeMillis(), Long.MAX_VALUE));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "FF"), new LegalInformation("me2",
			// "For other reasons", 0l, currentTimeMillis()));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type2", "01"), new LegalInformation("me3",
			// null, null, null));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "AA"), new LegalInformation("me",
			// "For reasons", 0l, Long.MAX_VALUE));
			// auditLogging.logStatisticsLookup(new DataTypeId("Type1", "AB"), new LegalInformation("me", "For
			// reasons", currentTimeMillis(), Long.MAX_VALUE));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "FF"), new LegalInformation("me2",
			// "For other reasons", 0l, currentTimeMillis()));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type2", "01"), new LegalInformation("me3",
			// null, null, null));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "AA"), new LegalInformation("me",
			// "For reasons", 0l, Long.MAX_VALUE));
			// auditLogging.logStatisticsLookup(new DataTypeId("Type1", "AB"), new LegalInformation("me", "For
			// reasons", currentTimeMillis(), Long.MAX_VALUE));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "FF"), new LegalInformation("me2",
			// "For other reasons", 0l, currentTimeMillis()));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type2", "01"), new LegalInformation("me3",
			// null, null, null));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "AA"), new LegalInformation("me",
			// "For reasons", 0l, Long.MAX_VALUE));
			// auditLogging.logStatisticsLookup(new DataTypeId("Type1", "AB"), new LegalInformation("me", "For
			// reasons", currentTimeMillis(), Long.MAX_VALUE));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "FF"), new LegalInformation("me2",
			// "For other reasons", 0l, currentTimeMillis()));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type2", "01"), new LegalInformation("me3",
			// null, null, null));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "AA"), new LegalInformation("me",
			// "For reasons", 0l, Long.MAX_VALUE));
			// auditLogging.logStatisticsLookup(new DataTypeId("Type1", "AB"), new LegalInformation("me", "For
			// reasons", currentTimeMillis(), Long.MAX_VALUE));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "FF"), new LegalInformation("me2",
			// "For other reasons", 0l, currentTimeMillis()));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type2", "01"), new LegalInformation("me3",
			// null, null, null));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "AA"), new LegalInformation("me",
			// "For reasons", 0l, Long.MAX_VALUE));
			// auditLogging.logStatisticsLookup(new DataTypeId("Type1", "AB"), new LegalInformation("me", "For
			// reasons", currentTimeMillis(), Long.MAX_VALUE));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type1", "FF"), new LegalInformation("me2",
			// "For other reasons", 0l, currentTimeMillis()));
			// auditLogging.logInvertedIndexLookup(new DataTypeId("Type2", "01"), new LegalInformation("me3",
			// null, null, null));
			// LOG.info("Done adding fake audit log data");
		}).start();
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Verify that looking up the specified selector in inverted index is legal based on the specified legal information", response = Legality.class)
	@RequestMapping(path = "/{dti_type}/{dti_uid}" + INVERTED_INDEX_PATH + LEGAL_RULES_PATH, method = GET)
	public ResponseEntity<?> checkLegalityOfInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final Legality response = legalRules.checkLegalityOfInvertedIndexLookup(selectorId, legalInfo);
			return ok(hateoasHelper.wrapInvertedIndexLegalStatus(response, selectorId, legalInfo));
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Verify that looking up statistics for the specified selector is legal based on the specified legal information", response = Legality.class)
	@RequestMapping(path = "/{dti_type}/{dti_uid}" + STATISTICS_PATH + LEGAL_RULES_PATH, method = GET)
	public ResponseEntity<?> checkLegalityOfStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
			final Legality response = legalRules.checkLegalityOfStatisticsLookup(selectorId, legalInfo);
			return ok(hateoasHelper.wrapStatisticsLegalStatus(response, selectorId, legalInfo));
		} else {
			return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Audit log that a lookup in inverted index is being executed, justified by the specified legal information (returns true, when log has been written to disk)", response = Boolean.class)
	@RequestMapping(path = "/{dti_type}/{dti_uid}" + INVERTED_INDEX_PATH + AUDIT_LOGGING_PATH, method = POST)
	public ResponseEntity<?> auditLogInvertedIndexLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		try {
			if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
				final int itemId = auditLogging.logInvertedIndexLookup(selectorId, legalInfo);
				webSocket.convertAndSend(MESSAGE_PREFIX + LEGAL_MESSAGE_PREFIX + HISTORY_UPDATED, "updated");
				return created(URI.create(urlLibrary.selectors().history().item(itemId))).build();
			} else {
				return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
			}
		} catch (final RuntimeException e) {
			LOG.error("Unable to complete audit logging", e);
			return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unable to complete request; adding log to storage failed.");
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Audit log that a lookup in selector statistics is being executed, justified by the specified legal information (returns true, when log has been written to disk)", response = Boolean.class)
	@RequestMapping(path = "/{dti_type}/{dti_uid}" + STATISTICS_PATH + AUDIT_LOGGING_PATH, method = POST)
	public ResponseEntity<?> auditLogStatisticsLookup(DataTypeId selectorId, LegalInformation legalInfo) {
		try {
			if (TYPE.isValid(selectorId.type) && UID.isValid(selectorId.uid)) {
				final int itemId = auditLogging.logStatisticsLookup(selectorId, legalInfo);
				webSocket.convertAndSend(MESSAGE_PREFIX + LEGAL_MESSAGE_PREFIX + HISTORY_UPDATED, "updated");
				return created(URI.create(urlLibrary.selectors().history().item(itemId))).build();
			} else {
				return badRequest().body(TYPE.getReason(selectorId.type) + "<BR>" + UID.getReason(selectorId.uid));
			}
		} catch (final RuntimeException e) {
			LOG.error("Unable to complete audit logging", e);
			return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Unable to complete request; adding log to storage failed.");
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch all previous audit logs from history", response = HistoryItemList.class)
	@RequestMapping(path = HISTORY_PATH, method = GET)
	public ResponseEntity<?> lookupAuditLoggingHistory(@PageableDefault Pageable pageable, PagedResourcesAssembler<HistoryItem> assembler) {
		final Page<HistoryItem> items = auditLogging.getPage(pageable);
		final Link selfLink = hateoasHelper.asPagedLink(REL_SELF, urlLibrary.selectors().history().all(), pageable);
		if (items.getSize() == 0) {
			return ok(assembler.toEmptyResource(items, HistoryItem.class, selfLink));
		} else {
			return ok(assembler.toResource(items, historyItemResourceAssembler, selfLink));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Fetch audit log item from history", response = HistoryItem.class)
	@RequestMapping(path = HISTORY_PATH + "/{itemId}", method = GET)
	public ResponseEntity<?> lookupAuditLoggingHistoryItem(@PathVariable Integer itemId) {
		final HistoryItem item = auditLogging.getItem(itemId);
		if (item != null) {
			return ok(hateoasHelper.wrap(item, auditLogging.getLastId()));
		} else {
			return notFound().build();
		}
	}

	public class HistoryItemResourceAssembler implements ResourceAssembler<HistoryItem, ResourceSupport> {
		@Override
		public ResourceSupport toResource(HistoryItem entity) {
			return hateoasHelper.wrap(entity, auditLogging.getLastId());
		}
	}
}