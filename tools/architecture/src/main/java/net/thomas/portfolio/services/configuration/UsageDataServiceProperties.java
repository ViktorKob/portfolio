package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Hard-coded configurations pending addition of config server
 */
public class UsageDataServiceProperties {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();

		// ####################
		// Service settings:
		// ####################

		put("service-context-path", "${usage-data-context-path}");
		put("service-name", "${usage-data-service-name}");
		put("service-status-page", "${external-protocol}service-user:password@${external-service-address}${service-context-path}/swagger-ui.html");

		// ####################
		// Unique settings:
		// ####################

		put("usage-data-service.hbaseIndexing.name", "${hbase-indexing-service-name}");
		put("usage-data-service.hbaseIndexing.credentials.user", "service-user");
		put("usage-data-service.hbaseIndexing.credentials.password", "password");

		put("usage-data-service.database.host", "localhost");
		put("usage-data-service.database.port", "3306");
		put("usage-data-service.database.schema", "usage_data");
		put("usage-data-service.database.user", "root");
		put("usage-data-service.database.password", "computer");

		// ####################
	}

	private static void put(String propertyId, String value) {
		SERVICE_PROPERTIES.put(propertyId, value);
	}

	public static void loadUsageDataConfigurationIntoProperties() {
		for (final Entry<String, String> property : SERVICE_PROPERTIES.entrySet()) {
			setProperty(property.getKey(), property.getValue());
		}
	}
}