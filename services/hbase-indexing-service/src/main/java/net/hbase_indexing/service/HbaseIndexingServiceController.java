package net.hbase_indexing.service;

import static net.architecture.globals.globals.ServiceGlobals.USAGE_DATA_SERVICE_PATH;
import static net.architecture.globals.globals.UsageDataServiceGlobals.FETCH_USAGE_ACTIVITY_PATH;
import static net.architecture.globals.globals.UsageDataServiceGlobals.STORE_USAGE_ACTIVITY_PATH;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.usage_data.UsageActivityType;

@Controller
@RequestMapping(USAGE_DATA_SERVICE_PATH)
public class HbaseIndexingServiceController {

	private final HbaseIndexingServiceConfiguration config;

	@Autowired
	public HbaseIndexingServiceController(HbaseIndexingServiceConfiguration config) {
		this.config = config;
	}

	@Secured("ROLE_SYSTEM")
	@RequestMapping(STORE_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> storeUsageActivity(String type, String uid, String username, UsageActivityType activityType, Long timeOfActivity) {
		return ResponseEntity.badRequest()
			.body("Both type and uid must be present. The uid must be of even length. Values: type=" + type + ", uid=" + uid);
	}

	@Secured("ROLE_SYSTEM")
	@RequestMapping(FETCH_USAGE_ACTIVITY_PATH)
	public ResponseEntity<?> fetchUsageActivity(String type, String uid) {
		return ResponseEntity.badRequest()
			.body("Both type and uid must be present. The uid must be of even length. Values: type=" + type + ", uid=" + uid);
	}
}