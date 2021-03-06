package net.thomas.portfolio.services;

import static java.lang.System.setProperty;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_MESSAGE_PREFIX;
import static net.thomas.portfolio.services.ServiceGlobals.ADMIN_SERVICE_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.ADMIN_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.ANALYTICS_SERVICE_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.ANALYTICS_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.CONFIG_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.CONFIG_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.INFRASTRUCTURE_MASTER_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.INFRASTRUCTURE_MASTER_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.MESSAGE_PREFIX;
import static net.thomas.portfolio.services.ServiceGlobals.NEXUS_SERVICE_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.NEXUS_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.RENDER_SERVICE_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.RENDER_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.USAGE_DATA_SERVICE_NAME;
import static net.thomas.portfolio.services.ServiceGlobals.USAGE_DATA_SERVICE_PATH;

public enum Service implements ContextPathSection {
	CONFIG_SERVICE("config-", CONFIG_PATH, CONFIG_NAME),
	INFRASTRUCTURE_SERVICE("infrastructure-", INFRASTRUCTURE_MASTER_PATH, INFRASTRUCTURE_MASTER_NAME),
	ADMIN_SERVICE("admin-", ADMIN_SERVICE_PATH, ADMIN_SERVICE_NAME),

	ANALYTICS_SERVICE("analytics-", ANALYTICS_SERVICE_PATH, ANALYTICS_SERVICE_NAME),
	HBASE_INDEXING_SERVICE("hbase-indexing-", HBASE_INDEXING_SERVICE_PATH, HBASE_INDEXING_SERVICE_NAME),
	LEGAL_SERVICE("legal-", LEGAL_SERVICE_PATH, LEGAL_SERVICE_NAME, MESSAGE_PREFIX + LEGAL_MESSAGE_PREFIX),
	NEXUS_SERVICE("nexus-", NEXUS_SERVICE_PATH, NEXUS_SERVICE_NAME),
	RENDER_SERVICE("render-", RENDER_SERVICE_PATH, RENDER_SERVICE_NAME),
	USAGE_DATA_SERVICE("usage-data-", USAGE_DATA_SERVICE_PATH, USAGE_DATA_SERVICE_NAME),
	USAGE_DATA_JPA_SERVICE("usage-data-jpa-", USAGE_DATA_SERVICE_PATH, USAGE_DATA_SERVICE_NAME);

	private final String propertyName;
	private final String contextPath;
	private final String serviceName;
	private final boolean needsWebSocketForwarding;
	private final String webSocketBroker;

	private Service(String propertyName, String contextPath, String serviceName) {
		this.propertyName = propertyName;
		this.contextPath = contextPath;
		this.serviceName = serviceName;
		needsWebSocketForwarding = false;
		webSocketBroker = null;
	}

	private Service(String propertyName, String contextPath, String serviceName, String webSocketBroker) {
		this.propertyName = propertyName;
		this.contextPath = contextPath;
		this.serviceName = serviceName;
		needsWebSocketForwarding = true;
		this.webSocketBroker = webSocketBroker;
	}

	@Override
	public String getContextPath() {
		return contextPath;
	}

	public String getServiceName() {
		return serviceName;
	}

	public boolean needsWebSocketForwarding() {
		return needsWebSocketForwarding;
	}

	public String getWebSocketBroker() {
		return webSocketBroker;
	}

	public static void loadServicePathsIntoProperties() {
		for (final Service endpoint : values()) {
			setProperty(endpoint.propertyName + "context-path", endpoint.contextPath);
			setProperty(endpoint.propertyName + "service-name", endpoint.serviceName);
		}
	}
}
