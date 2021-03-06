package net.thomas.portfolio.analytics.service;

import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_PATH;
import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_ROOT_PATH;
import static net.thomas.portfolio.service_commons.network.urls.UrlFactory.usingPortfolio;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.thomas.portfolio.analytics.system.AnalyticsControl;
import net.thomas.portfolio.common.services.parameters.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@RestController
@Api(value = "", description = "Interaction with the Analytical systems")
@EnableConfigurationProperties
@RequestMapping(produces = "application/hal+json")
public class AnalyticsServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);

	private final AnalyticsServiceConfiguration config;
	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;

	private AnalyticsControl analyticsSystem;
	private PortfolioHateoasWrappingHelper hateoasHelper;

	public AnalyticsServiceController(AnalyticsServiceConfiguration config) {
		this.config = config;
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean(name = "HbaseIndexModelAdaptor")
	public HbaseIndexModelAdaptor getHbaseIndexModelAdaptor() {
		return new HbaseIndexModelAdaptorImpl();
	}

	@PostConstruct
	public void initialize() {
		final UrlFactory urlFactory = new UrlFactory(() -> {
			return globalUrlPrefix;
		}, new PortfolioUrlSuffixBuilder());
		hateoasHelper = new PortfolioHateoasWrappingHelper(urlFactory);
		new Thread(() -> {
			((PortfolioInfrastructureAware) hbaseAdaptor).initialize(new PortfolioUrlLibrary(usingPortfolio(discoveryClient, config.getHbaseIndexing())),
					new HttpRestClient(restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getDataTypes());
		}).start();
		analyticsSystem = new AnalyticsControl();
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Lookup knowledge about this specific selector in the analytical systems", response = AnalyticalKnowledge.class)
	@RequestMapping(path = LOOKUP_KNOWLEDGE_ROOT_PATH + "/{dti_type}/{dti_uid}" + LOOKUP_KNOWLEDGE_PATH, method = GET)
	public ResponseEntity<?> lookupPriorKnowledge(DataTypeId id) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid)) {
			return ok(hateoasHelper.wrap(analyticsSystem.getPriorKnowledge(id), id));
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid));
		}
	}
}