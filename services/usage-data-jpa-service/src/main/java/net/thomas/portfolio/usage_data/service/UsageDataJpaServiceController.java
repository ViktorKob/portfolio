package net.thomas.portfolio.usage_data.service;

import static java.lang.Integer.MAX_VALUE;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.USAGE_ACTIVITIES_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.USAGE_ACTIVITIES_ROOT_PATH;
import static net.thomas.portfolio.service_commons.network.urls.UrlFactory.usingPortfolio;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
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
import net.thomas.portfolio.common.services.parameters.validation.EnumValueValidator;
import net.thomas.portfolio.common.services.parameters.validation.IntegerRangeValidator;
import net.thomas.portfolio.common.services.parameters.validation.LongRangeValidator;
import net.thomas.portfolio.common.services.parameters.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.common.services.parameters.validation.StringPresenceValidator;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;
import net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlSuffixBuilder;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;
import net.thomas.portfolio.usage_data.service.storage.UsageDataJpaStorage;

@RestController
@Api(value = "", description = "Lookup of previous analysts interaction with documents")
@EnableConfigurationProperties
@RequestMapping(value = USAGE_ACTIVITIES_ROOT_PATH + "/{dti_type}/{dti_uid}" + USAGE_ACTIVITIES_PATH, produces = "application/hal+json")
public class UsageDataJpaServiceController {
	private static final Logger LOG = getLogger(UsageDataJpaServiceController.class);
	private static final long AROUND_THOUSAND_YEARS_AGO = -1000l * 60 * 60 * 24 * 365 * 1000;
	private static final long AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW = 1000l * 60 * 60 * 24 * 365 * 8000;
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);
	private static final StringPresenceValidator USERNAME = new StringPresenceValidator("uai_user", true);
	private static final EnumValueValidator<UsageActivityType> USAGE_ACTIVITY_TYPE = new EnumValueValidator<>("uai_type", UsageActivityType.values(), true);
	private static final IntegerRangeValidator OFFSET = new IntegerRangeValidator("offset", 0, MAX_VALUE, false);
	private static final IntegerRangeValidator LIMIT = new IntegerRangeValidator("limit", 1, MAX_VALUE, false);
	private static final LongRangeValidator TIME_OF_ACTIVITY = new LongRangeValidator("uai_timeOfActivity", Long.MIN_VALUE, Long.MAX_VALUE, false);

	private final UsageDataJpaServiceConfiguration config;
	@Value("${global-url-prefix}")
	private String globalUrlPrefix;
	@Value("${spring.datasource.url}")
	private String url;

	@Autowired
	private EurekaClient discoveryClient;
	@Autowired
	private HbaseIndexModelAdaptor hbaseAdaptor;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private UsageDataJpaStorage storage;

	private PortfolioHateoasWrappingHelper hateoasHelper;

	@Autowired
	public UsageDataJpaServiceController(UsageDataJpaServiceConfiguration config) {
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
	public void initializeService() {
		System.out.println("Database located at: " + url);
		final UrlFactory urlFactory = new UrlFactory(() -> {
			return globalUrlPrefix;
		}, new PortfolioUrlSuffixBuilder());
		hateoasHelper = new PortfolioHateoasWrappingHelper(urlFactory);
		new Thread(() -> {
			((PortfolioInfrastructureAware) hbaseAdaptor).initialize(new PortfolioUrlLibrary(usingPortfolio(discoveryClient, config.getHbaseIndexing())),
					new HttpRestClient(restTemplate, config.getHbaseIndexing()));
			TYPE.setValidStrings(hbaseAdaptor.getDocumentTypes());
		}).start();
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Lookup usage interaction with this document", response = UsageActivities.class)
	@RequestMapping(method = GET)
	public ResponseEntity<?> fetchUsageActivity(DataTypeId id, Bounds bounds) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid) && OFFSET.isValid(bounds.offset) && LIMIT.isValid(bounds.limit)) {
			bounds.replaceMissing(0, 20, AROUND_THOUSAND_YEARS_AGO, AROUND_EIGHT_THOUSAND_YEARS_FROM_NOW);
			try {
				final UsageActivities activities = storage.getActivitiesFor(id, bounds);
				if (activities != null) {
					return ok(hateoasHelper.wrap(activities, id, bounds));
				} else {
					return notFound().build();
				}
			} catch (final Throwable t) {
				t.printStackTrace();
				return status(INTERNAL_SERVER_ERROR).body("The server was unable to complete the request.");
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid) + "<BR>" + OFFSET.getReason(bounds.offset) + "<BR>"
					+ LIMIT.getReason(bounds.limit));
		}
	}

	@Secured("ROLE_USER")
	@ApiOperation(value = "Add usage interaction (returns a copy of the information stored)", response = UsageActivities.class)
	@RequestMapping(method = POST)
	public ResponseEntity<?> storeUsageActivity(DataTypeId id, UsageActivity activity) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid) && USERNAME.isValid(activity.user) && USAGE_ACTIVITY_TYPE.isValid(activity.type)
				&& TIME_OF_ACTIVITY.isValid(activity.timeOfActivity)) {
			try {
				storage.storeActivity(id, activity);
				return ok(hateoasHelper.wrap(activity, id));
			} catch (final Throwable t) {
				t.printStackTrace();
				return status(INTERNAL_SERVER_ERROR).body("The server was unable to store the activity.");
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid) + "<BR>" + USERNAME.getReason(activity.user) + "<BR>"
					+ USAGE_ACTIVITY_TYPE.getReason(activity.type) + "<BR>" + TIME_OF_ACTIVITY.getReason(activity.timeOfActivity));
		}
	}
}