package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.thomas.portfolio.services.Service;

/***
 * Hard-coded configurations pending addition of config server
 */
public class ProxyServiceParameters {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();

		put("service-name", "proxy-service");
		put("service-status-page", "${external-protocol}service-user:password@${external-service-address}/proxy/actuator/health");

		// ####################
		// Unique settings:
		// ####################

		put("zuul.ignored-services", "*");
		put("zuul.sensitive-headers", "");

		for (final Service service : Service.values()) {
			addServiceToProxy(service);
		}

		put("zuul.host.connect-timeout-millis", "2000");
		put("zuul.host.socket-timeout-millis", "20000");
		put("zuul.host.max-total-connections", "10000");
		put("zuul.host.max-per-router-connections", "2000");
		put("zuul.host.time-to-live", "2000");
		put("zuul.host.time-unit", "MILLISECONDS");
		put("ribbon.eureka.enabled", "true");
		put("ribbon.ConnectTimeout", "2000");
		put("ribbon.ReadTimeout", "600000");
		put("hystrix.command.default.execution.timeout.enabled", "false");

		// ####################
		// Standard settings:
		// ####################

		put("server.port", "${service-port}");
		put("server.max-http-header-size", "200000");

		put("server.tomcat.max-connections", "2000");
		put("server.tomcat.max-http-post-size", "200000");
		put("server.tomcat.max-threads", "50");
		put("server.tomcat.min-spare-threads", "2");

		put("spring.application.name", "${service-name}");
		put("spring.security.user.name", "service-user");
		put("spring.security.user.password", "password");
		put("spring.security.user.roles", "USER");

		put("management.endpoints.web.base-path", "/proxy/actuator");
		put("management.endpoints.web.cors.allowed-origins", "true");
		put("management.endpoints.web.exposure.include", "*");
		put("management.endpoint.health.show-details", "ALWAYS");

		put("eureka.instance.lease-renewal-interval-in-seconds", "5");
		put("eureka.instance.lease-expiration-duration-in-seconds", "10");
		put("eureka.instance.health-check-url-path", "/proxy/actuator/health");
		put("eureka.instance.status-page-url-path", "${service-status-page}");

		put("eureka.instance.metadata-map.management.context-path", "/proxy/actuator");
		put("eureka.instance.metadata-map.user.name", "${spring.security.user.name}");
		put("eureka.instance.metadata-map.user.password", "${spring.security.user.password}");

		put("eureka.client.register-with-eureka", "true");
		put("eureka.client.fetch-registry", "true");
		put("eureka.client.registry-fetch-interval-seconds", "5");
		put("eureka.client.service-url.defaultZone", "http://service-user:password@${discovery-address}${infrastructure-context-path}/eureka/");

		// ####################
	}

	private static void addServiceToProxy(final Service service) {
		put("zuul.routes." + service.name() + ".path", service.getContextPath() + "/**");
		put("zuul.routes." + service.name() + ".strip-prefix", "false");
		put("zuul.routes." + service.name() + ".service-id", service.getServiceName());
	}

	private static void put(String propertyId, String value) {
		SERVICE_PROPERTIES.put(propertyId, value);
	}

	public static void loadProxyServiceConfigurationIntoProperties() {
		for (final Entry<String, String> property : SERVICE_PROPERTIES.entrySet()) {
			setProperty(property.getKey(), property.getValue());
		}
	}
}
