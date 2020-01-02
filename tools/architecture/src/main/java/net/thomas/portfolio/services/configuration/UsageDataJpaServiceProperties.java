package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Hard-coded configurations pending addition of config server
 */
public class UsageDataJpaServiceProperties {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();

		// ####################
		// Service settings:
		// ####################

		put("service-context-path", "${usage-data-jpa-context-path}");
		put("service-name", "${usage-data-jpa-service-name}");
		put("service-status-page", "${external-protocol}service-user:password@${external-service-address}${service-context-path}/swagger-ui.html");

		// ####################
		// Unique settings:
		// ####################

		put("usage-data-service.hbaseIndexing.name", "${hbase-indexing-service-name}");
		put("usage-data-service.hbaseIndexing.credentials.user", "service-user");
		put("usage-data-service.hbaseIndexing.credentials.password", "password");

		put("usage-data-service.database.database_name", "usage_data");

		put("spring.jpa.hibernate.ddl-auto", "none");
		put("spring.datasource.url", "jdbc:h2:file:${user.dir}/database/${usage-data-service.database.database_name};mode=mysql");
		put("spring.datasource.driver-class-name", "org.h2.Driver");
		put("spring.datasource.username", "db-user");
		put("spring.datasource.password", "password");
		put("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		// ####################
	}

	private static void put(String propertyId, String value) {
		SERVICE_PROPERTIES.put(propertyId, value);
	}

	public static void loadUsageDataJpaConfigurationIntoProperties() {
		for (final Entry<String, String> property : SERVICE_PROPERTIES.entrySet()) {
			setProperty(property.getKey(), property.getValue());
		}
	}
}