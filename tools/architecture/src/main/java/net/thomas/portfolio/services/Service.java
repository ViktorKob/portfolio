package net.thomas.portfolio.services;

import static java.lang.System.setProperty;
import static net.thomas.portfolio.services.ServiceGlobals.ADMIN_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.ANALYTICS_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.INFRASTRUCTURE_MASTER_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.NEXUS_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.RENDER_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.USAGE_DATA_SERVICE_PATH;

public enum Service implements ServiceEndpoint {
	INFRASTRUCTURE_SERVICE("infrastructure-context-path", INFRASTRUCTURE_MASTER_PATH),
	ADMIN_SERVICE("admin-context-path", ADMIN_SERVICE_PATH),

	ANALYTICS_SERVICE("analytics-context-path", ANALYTICS_SERVICE_PATH),
	HBASE_INDEXING_SERVICE("hbase-indexing-context-path", HBASE_INDEXING_SERVICE_PATH),
	LEGAL_SERVICE("legal-context-path", LEGAL_SERVICE_PATH),
	NEXUS_SERVICE("nexus-context-path", NEXUS_SERVICE_PATH),
	RENDER_SERVICE("render-context-path", RENDER_SERVICE_PATH),
	USAGE_DATA_SERVICE("usage-data-context-path", USAGE_DATA_SERVICE_PATH);

	private final String propertyName;
	private final String path;

	private Service(String propertyName, String path) {
		this.propertyName = propertyName;
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}

	public static void loadServicePathsIntoProperties() {
		for (final Service endpoint : values()) {
			setProperty(endpoint.propertyName, endpoint.getPath());
		}
	}
}
