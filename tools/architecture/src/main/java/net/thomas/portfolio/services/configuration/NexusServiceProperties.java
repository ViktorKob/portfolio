package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Hard-coded configurations pending addition of config server
 */
public class NexusServiceProperties {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();

		// ####################
		// Service settings:
		// ####################

		put("service-context-path", "${nexus-context-path}");
		put("service-name", "${nexus-service-name}");
		// GraphiQL does not support final credentials in url
		put("service-status-page", "${external-protocol}${external-service-address}${nexus-context-path}/graphiql");

		// ####################
		// Unique settings:
		// ####################

		put("graphiql.endpoint", "${nexus-context-path}/graphql");
		put("management.endpoints.jmx.exposure.include", "*");

		put("nexus-service.analytics.name", "${analytics-service-name}");
		put("nexus-service.analytics.credentials.user", "service-user");
		put("nexus-service.analytics.credentials.password", "password");

		put("nexus-service.hbaseIndexing.name", "${hbase-indexing-service-name}");
		put("nexus-service.hbaseIndexing.credentials.user", "service-user");
		put("nexus-service.hbaseIndexing.credentials.password", "password");

		put("nexus-service.legal.name", "${legal-service-name}");
		put("nexus-service.legal.credentials.user", "service-user");
		put("nexus-service.legal.credentials.password", "password");

		put("nexus-service.rendering.name", "${render-service-name}");
		put("nexus-service.rendering.credentials.user", "service-user");
		put("nexus-service.rendering.credentials.password", "password");

		put("nexus-service.usage.name", "${usage-data-service-name}");
		put("nexus-service.usage.credentials.user", "service-user");
		put("nexus-service.usage.credentials.password", "password");

		put("logging.level.org.springframework", "INFO");
		put("logging.level.springfox", "INFO");

		put("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", "5000");
		put("hystrix.command.default.circuitBreaker.requestVolumeThreshold", "5");
		put("hystrix.command.default.circuitBreaker.sleepWindowInMilliseconds", "1000");
		put("hystrix.command.default.metrics.rollingStats.timeInMilliseconds", "300000");
		put("hystrix.command.default.metrics.rollingStats.numBuckets", "300");

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