package net.thomas.portfolio.usage_data.service;

import static java.lang.Integer.MAX_VALUE;
import static net.thomas.portfolio.enums.HbaseDataServiceEndpoint.GET_SCHEMA;
import static net.thomas.portfolio.enums.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.globals.ServiceGlobals.USAGE_DATA_SERVICE_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.FETCH_USAGE_ACTIVITY_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.STORE_USAGE_ACTIVITY_PATH;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.badRequest;

import java.util.List;
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

import net.thomas.portfolio.common.services.validation.EnumValueValidator;
import net.thomas.portfolio.common.services.validation.IntegerRangeValidator;
import net.thomas.portfolio.common.services.validation.LongRangeValidator;
import net.thomas.portfolio.common.services.validation.SpecificStringPresenceValidator;
import net.thomas.portfolio.common.services.validation.StringPresenceValidator;
import net.thomas.portfolio.common.services.validation.UidValidator;
import net.thomas.portfolio.service_commons.services.HttpRestClient;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;
import net.thomas.portfolio.usage_data.UsageActivityItem;
import net.thomas.portfolio.usage_data.UsageActivityType;
import net.thomas.portfolio.usage_data.sql.SqlProxy;

@Controller
@RequestMapping(USAGE_DATA_SERVICE_PATH)
public class UsageServiceController {
	private static final SpecificStringPresenceValidator TYPE = new SpecificStringPresenceValidator("type", true);
	private static final UidValidator UID = new UidValidator("uid", true);
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
		final HttpRestClient hbaseIndexClient = new HttpRestClient(discoveryClient, getRestTemplate(), config.getHbaseIndexing());
		final Set<String> documentTypes = hbaseIndexClient.loadUrlAsObject(HBASE_INDEXING_SERVICE, GET_SCHEMA, HBaseIndexSchemaSerialization.class)
			.getDocumentTypes();
		TYPE.setValidStrings(documentTypes);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		final RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	@Secured("ROLE_USER")
	@RequestMapping(STORE_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> storeUsageActivity(String type, String uid, String username, UsageActivityType activityType, Long timeOfActivity) {
		if (TYPE.isValid(type) && UID.isValid(uid) && USERNAME.isValid(username) && USAGE_ACTIVITY_TYPE.isValid(activityType)
				&& TIME_OF_ACTIVITY.isValid(timeOfActivity)) {
			if (timeOfActivity == null) {
				timeOfActivity = System.currentTimeMillis();
			}
			try {
				proxy.storeUsageActivity(uid, type, username, activityType, timeOfActivity);
				return ResponseEntity.ok("Activity stored.");
			} catch (final Throwable t) {
				t.printStackTrace();
				return ResponseEntity.status(INTERNAL_SERVER_ERROR)
					.body("The server was unable to complete the request.");
			}
		} else {
			return badRequest().body(TYPE.getReason(type) + "<BR>" + UID.getReason(uid) + "<BR>" + USERNAME.getReason(username) + "<BR>"
					+ USAGE_ACTIVITY_TYPE.getReason(activityType) + "<BR>" + TIME_OF_ACTIVITY.getReason(timeOfActivity));
		}
	}

	@Secured("ROLE_USER")
	@RequestMapping(FETCH_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> fetchUsageActivity(String type, String uid, Integer offset, Integer limit) {
		if (TYPE.isValid(type) && UID.isValid(uid) && OFFSET.isValid(offset) && LIMIT.isValid(limit)) {
			if (offset == null) {
				offset = 0;
			}
			if (limit == null) {
				limit = 20;
			}
			try {
				final List<UsageActivityItem> activities = proxy.fetchUsageActivities(uid, type, offset, limit);
				return ResponseEntity.ok(activities);
			} catch (final Throwable t) {
				t.printStackTrace();
				return ResponseEntity.status(INTERNAL_SERVER_ERROR)
					.body("The server was unable to complete the request.");
			}
		} else {
			return badRequest().body(TYPE.getReason(type) + "<BR>" + UID.getReason(uid) + "<BR>" + OFFSET.getReason(offset) + "<BR>" + LIMIT.getReason(limit));
		}
	}
}