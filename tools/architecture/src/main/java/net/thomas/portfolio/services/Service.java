package net.thomas.portfolio.services;

import static net.thomas.portfolio.services.ServiceGlobals.ANALYTICS_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.NEXUS_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.RENDER_SERVICE_PATH;
import static net.thomas.portfolio.services.ServiceGlobals.USAGE_DATA_SERVICE_PATH;

public enum Service implements ServiceEndpoint {
	ANALYTICS_SERVICE(ANALYTICS_SERVICE_PATH), HBASE_INDEXING_SERVICE(HBASE_INDEXING_SERVICE_PATH), LEGAL_SERVICE(LEGAL_SERVICE_PATH), NEXUS_SERVICE(
			NEXUS_SERVICE_PATH), RENDER_SERVICE(RENDER_SERVICE_PATH), USAGE_DATA_SERVICE(USAGE_DATA_SERVICE_PATH);

	private final String path;

	private Service(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
