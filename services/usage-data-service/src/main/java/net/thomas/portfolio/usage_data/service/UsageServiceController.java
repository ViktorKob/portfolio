package net.thomas.portfolio.usage_data.service;

import static java.lang.Integer.MAX_VALUE;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.FETCH_USAGE_ACTIVITY_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.STORE_USAGE_ACTIVITY_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.USAGE_DATA_SERVICE_PATH;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.validation.EnumValueValidator;
import net.thomas.portfolio.common.services.validation.IntegerRangeValidator;
import net.thomas.portfolio.common.services.validation.LongRangeValidator;
import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.common.services.validation.StringPresenceValidator;
import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.service_commons.validation.UidValidator;
import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityItem;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;
import net.thomas.portfolio.usage_data.sql.SqlProxy;

@Controller
@RequestMapping(USAGE_DATA_SERVICE_PATH)
public class UsageServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("dti_type", true);
	private static final UidValidator UID = new UidValidator("dti_uid", true);
	private static final StringPresenceValidator USERNAME = new StringPresenceValidator("username", true);
	private static final EnumValueValidator<UsageActivityType> USAGE_ACTIVITY_TYPE = new EnumValueValidator<>("activityType", UsageActivityType.values(), true);
	private static final IntegerRangeValidator OFFSET = new IntegerRangeValidator("offset", 0, MAX_VALUE, false);
	private static final IntegerRangeValidator LIMIT = new IntegerRangeValidator("limit", 1, MAX_VALUE, false);
	private static final LongRangeValidator TIME_OF_ACTIVITY = new LongRangeValidator("timeOfActivity", Long.MIN_VALUE, Long.MAX_VALUE, false);

	@Autowired
	private EurekaClient discoveryClient;

	private final UsageDataServiceConfiguration config;
	private final SqlProxy proxy;

	@Autowired
	public UsageServiceController(UsageDataServiceConfiguration config) {
		this.config = config;
		proxy = new SqlProxy(config.getDatabase());
	}

	@PostConstruct
	public void ensureDatabaseIsReady() {
		proxy.ensurePresenceOfSchema();
	}

	@PostConstruct
	public void loadHbaseIndexingSchema() {
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
	@RequestMapping(STORE_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> storeUsageActivity(DataTypeId id, String username, UsageActivityType activityType, Long timeOfActivity) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid) && USERNAME.isValid(username) && USAGE_ACTIVITY_TYPE.isValid(activityType)
				&& TIME_OF_ACTIVITY.isValid(timeOfActivity)) {
			if (timeOfActivity == null) {
				timeOfActivity = System.currentTimeMillis();
			}
			try {
				proxy.storeUsageActivity(id, username, activityType, timeOfActivity);
				return ResponseEntity.ok("Activity stored.");
			} catch (final Throwable t) {
				t.printStackTrace();
				return ResponseEntity.status(INTERNAL_SERVER_ERROR)
					.body("The server was unable to complete the request.");
			}
		} else {
			return badRequest().body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid) + "<BR>" + USERNAME.getReason(username) + "<BR>"
					+ USAGE_ACTIVITY_TYPE.getReason(activityType) + "<BR>" + TIME_OF_ACTIVITY.getReason(timeOfActivity));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(FETCH_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> fetchUsageActivity(DataTypeId id, Integer offset, Integer limit) {
		if (TYPE.isValid(id.type) && UID.isValid(id.uid) && OFFSET.isValid(offset) && LIMIT.isValid(limit)) {
			if (offset == null) {
				offset = 0;
			}
			if (limit == null) {
				limit = 20;
			}
			try {
				final Collection<UsageActivityItem> activities = proxy.fetchUsageActivities(id, offset, limit);
				if (activities != null) {
					return ResponseEntity.ok(activities);
				} else {
					return ResponseEntity.notFound()
						.build();
				}
			} catch (final Throwable t) {
				t.printStackTrace();
				return ResponseEntity.status(INTERNAL_SERVER_ERROR)
					.body("The server was unable to complete the request.");
			}
		} else {
			return badRequest()
				.body(TYPE.getReason(id.type) + "<BR>" + UID.getReason(id.uid) + "<BR>" + OFFSET.getReason(offset) + "<BR>" + LIMIT.getReason(limit));
		}
	}
}