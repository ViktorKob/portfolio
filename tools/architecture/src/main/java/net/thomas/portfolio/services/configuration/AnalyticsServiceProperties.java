package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Hard-coded configurations pending addition of config server
 */
public class AnalyticsServiceProperties {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();

		// ####################
		// Service settings:
		// ####################

		put("service-context-path", "${analytics-context-path}");
		put("service-name", "${analytics-service-name}");
		put("service-status-page", "${external-protocol}service-user:password@${external-service-address}${service-context-path}/swagger-ui.html");

		// ####################
		// Unique settings:
		// ####################

		put("analytics-service.hbaseIndexing.name", "${hbase-indexing-service-name}");
		put("analytics-service.hbaseIndexing.credentials.user", "service-user");
		put("analytics-service.hbaseIndexing.credentials.password", "password");

		// ####################
	}

	private static void put(String propertyId, String value) {
		SERVICE_PROPERTIES.put(propertyId, value);
	}

	public static void loadAnalyticsConfigurationIntoProperties() {
		for (final Entry<String, String> property : SERVICE_PROPERTIES.entrySet()) {
			setProperty(property.getKey(), property.getValue());
		}
	}
}