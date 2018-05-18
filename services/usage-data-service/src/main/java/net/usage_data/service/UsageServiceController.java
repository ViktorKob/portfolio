package net.usage_data.service;

import static net.architecture.globals.globals.ServiceGlobals.USAGE_DATA_SERVICE_PATH;
import static net.architecture.globals.globals.UsageDataServiceGlobals.FETCH_USAGE_ACTIVITY_PATH;
import static net.architecture.globals.globals.UsageDataServiceGlobals.STORE_USAGE_ACTIVITY_PATH;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.usage_data.UsageActivityItem;
import net.usage_data.UsageActivityType;
import net.usage_data.sql.SqlProxy;

@Controller
@RequestMapping(USAGE_DATA_SERVICE_PATH)
public class UsageServiceController {

	private final SqlProxy proxy;

	@Autowired
	public UsageServiceController(UsageDataServiceConfiguration config) {
		proxy = new SqlProxy(config.getDatabase());
	}

	@Secured("ROLE_USER")
	@RequestMapping(STORE_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> storeUsageActivity(String type, String uid, String username, UsageActivityType activityType, Long timeOfActivity) {
		if (validateTypeAndUid(type, uid) && validateActivity(username, activityType)) {
			return ResponseEntity.badRequest()
				.body("Both type and uid must be present. The uid must be of even length. Values: type=" + type + ", uid=" + uid);
		}
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
	}

	@Secured("ROLE_USER")
	@RequestMapping(FETCH_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> fetchUsageActivity(String type, String uid, Integer offset, Integer limit) {
		if (validateTypeAndUid(type, uid)) {
			return ResponseEntity.badRequest()
				.body("Both type and uid must be present. The uid must be of even length. Values: type=" + type + ", uid=" + uid);
		}
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
	}

	private boolean validateTypeAndUid(String type, String uid) {
		return type == null || uid == null || uid.length() % 2 == 1;
	}

	private boolean validateActivity(String username, UsageActivityType activityType) {
		return username != null && !username.isEmpty() && activityType != null;
	}
}