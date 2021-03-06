package net.thomas.portfolio.services.configuration;

import static java.lang.System.setProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.thomas.portfolio.services.Service;

/***
 * Hard-coded configurations pending addition of config server
 */
public class ProxyServiceProperties {

	public static final Map<String, String> SERVICE_PROPERTIES;
	static {
		SERVICE_PROPERTIES = new HashMap<>();

		put("service-context-path", "/Proxy");
		put("service-name", "proxy");

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
		// put("hystrix.command.default.execution.timeout.enabled", "false");

		// ####################
		// Standard settings:
		// ####################
		put("global-url-prefix", "${external-protocol}${external-service-address}");

		put("server.port", "${service-port}");
		put("server.max-http-header-size", "200000");
		put("server.use-forward-header", "true");

		put("server.tomcat.max-connections", "2000");
		put("server.tomcat.max-http-post-size", "200000");
		put("server.tomcat.max-threads", "50");
		put("server.tomcat.min-spare-threads", "2");

		put("spring.application.name", "${service-name}");
		put("spring.security.user.name", "service-user");
		put("spring.security.user.password", "password");
		put("spring.security.user.roles", "USER");

		put("management.endpoints.web.base-path", "${service-context-path}/actuator");
		put("management.endpoints.web.cors.allowed-origins", "true");
		put("management.endpoints.web.exposure.include", "*");
		put("management.endpoint.health.show-details", "ALWAYS");

		put("eureka.instance.prefer-ip-address", "true");
		put("eureka.instance.lease-renewal-interval-in-seconds", "5");
		put("eureka.instance.lease-expiration-duration-in-seconds", "10");
		put("eureka.instance.health-check-url-path", "${service-context-path}/actuator/health");
		put("eureka.instance.status-page-url-path", "${service-status-page}");

		put("eureka.instance.metadata-map.management.context-path", "${service-context-path}/actuator");
		put("eureka.instance.metadata-map.user.name", "${spring.security.user.name}");
		put("eureka.instance.metadata-map.user.password", "${spring.security.user.password}");

		put("eureka.client.register-with-eureka", "true");
		put("eureka.client.fetch-registry", "true");
		put("eureka.client.registry-fetch-interval-seconds", "5");
		put("eureka.client.service-url.defaultZone", "http://service-user:password@${discovery-address}${infrastructure-context-path}/eureka/");

		put("service-status-page", "${external-protocol}service-user:password@${external-service-address}${service-context-path}/actuator/routes");

		// ####################
	}

	private static void addServiceToProxy(final Service service) {
		put("zuul.routes." + service.name() + ".path", service.getContextPath() + "/**");
		put("zuul.routes." + service.name() + ".strip-prefix", "false");
		put("zuul.routes." + service.name() + ".service-id", service.getServiceName());
		if (service.needsWebSocketForwarding()) {
			put("zuul.ws.brokerages." + service.name() + ".end-points", "/stomp");
			put("zuul.ws.brokerages." + service.name() + ".brokers", service.getWebSocketBroker());
			put("zuul.ws.brokerages." + service.name() + ".destination-prefixes", "/stomp");
		}
	}

	private static void put(final String propertyId, final String value) {
		SERVICE_PROPERTIES.put(propertyId, value);
	}

	public static void loadProxyServiceConfigurationIntoProperties() {
		for (final Entry<String, String> property : SERVICE_PROPERTIES.entrySet()) {
			setProperty(property.getKey(), property.getValue());
		}
	}
}
