package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/***
 * Hard-coded configurations pending addition of config server
 */
public class InfrastructureServiceParameters {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();
		// ####################
		// Service settings:
		// ####################

		put("service-context-path", "${infrastructure-context-path}");
		put("service-name", "${infrastructure-service-name}");
		put("service-status-page",
				"${external-protocol}service-user:password@${external-service-address}${infrastructure-context-path}/hystrix/monitor?stream="
						+ "http%3A%2F%2Fservice-user%3Apassword%40localhost%3A8100%2FNexusService%2Factuator%2F"
						+ "hystrix.stream&delay=1000&title=Nexus%20Service%20Monitor");

		// ####################
		// Unique settings:
		// ####################

		put("eureka.server.enableSelfPreservation", "false");
		put("ribbon.eureka.enabled", "true");
		put("ribbon.eureka.ConnectTimeout", "2000");
		put("ribbon.eureka.ReadTimeout", "600000");

		// ####################
		// Standard settings:
		// ####################

		put("server.port", "${service-port}");
		put("server.max-http-header-size", "200000");
		put("server.servlet.context-path", "${service-context-path}");

		put("server.tomcat.max-connections", "2000");
		put("server.tomcat.max-http-post-size", "200000");
		put("server.tomcat.max-threads", "50");
		put("server.tomcat.min-spare-threads", "2");

		put("spring.application.name", "${service-name}");
		put("spring.security.user.name", "service-user");
		put("spring.security.user.password", "password");
		put("spring.security.user.roles", "USER");

		put("management.endpoints.web.base-path", "/actuator");
		put("management.endpoints.web.cors.allowed-origins", "true");
		put("management.endpoints.web.exposure.include", "*");
		put("management.endpoint.health.show-details", "ALWAYS");

		put("eureka.instance.lease-renewal-interval-in-seconds", "5");
		put("eureka.instance.lease-expiration-duration-in-seconds", "10");
		put("eureka.instance.health-check-url-path", "${service-context-path}/actuator/health");
		put("eureka.instance.status-page-url-path", "${service-status-page}");

		put("eureka.instance.metadata-map.management.context-path", "${service-context-path}/actuator");
		put("eureka.instance.metadata-map.user.name", "${spring.security.user.name}");
		put("eureka.instance.metadata-map.user.password", "${spring.security.user.password}");

		put("eureka.client.register-with-eureka", "true");
		put("eureka.client.fetch-registry", "false");
		put("eureka.client.registry-fetch-interval-seconds", "5");
		put("eureka.client.service-url.defaultZone", "http://service-user:password@${discovery-address}${infrastructure-context-path}/eureka/");

		// ####################

	}

	private static void put(String propertyId, String value) {
		SERVICE_PROPERTIES.put(propertyId, value);
	}

	public static void loadInfrastructureServiceConfigurationIntoProperties() {
		for (final Entry<String, String> property : SERVICE_PROPERTIES.entrySet()) {
			setProperty(property.getKey(), property.getValue());
		}
	}
}
