package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Hard-coded configurations pending addition of config server
 */
public class LegalServiceProperties {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();

		// ####################
		// Service settings:
		// ####################

		put("service-context-path", "${legal-context-path}");
		put("service-name", "${legal-service-name}");
		put("service-status-page", "${external-service-address}${service-context-path}/actuator/health");

		// ####################
		// Unique settings:
		// ####################

		put("legal-service.analytics.name", "${analytics-service-name}");
		put("legal-service.analytics.credentials.user", "service-user");
		put("legal-service.analytics.credentials.password", "password");

		put("legal-service.hbaseIndexing.name", "${hbase-indexing-service-name}");
		put("legal-service.hbaseIndexing.credentials.user", "service-user");
		put("legal-service.hbaseIndexing.credentials.password", "password");

		// ####################
	}

	private static void put(String propertyId, String value) {
		SERVICE_PROPERTIES.put(propertyId, value);
	}

	public static void loadGenericConfigurationIntoProperties() {
		for (final Entry<String, String> property : SERVICE_PROPERTIES.entrySet()) {
			setProperty(property.getKey(), property.getValue());
		}
	}
}