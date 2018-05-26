package net.thomas.portfolio.entities;

import static net.thomas.portfolio.entities.ServiceGlobals.ANALYTICS_SERVICE_PATH;
import static net.thomas.portfolio.entities.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static net.thomas.portfolio.entities.ServiceGlobals.LEGAL_SERVICE_PATH;
import static net.thomas.portfolio.entities.ServiceGlobals.RENDER_SERVICE_PATH;
import static net.thomas.portfolio.entities.ServiceGlobals.USAGE_DATA_SERVICE_PATH;

public enum Service implements ServiceEndpoint {
	HBASE_INDEXING_SERVICE(HBASE_INDEXING_SERVICE_PATH), RENDER_SERVICE(RENDER_SERVICE_PATH), USAGE_DATA_SERVICE(USAGE_DATA_SERVICE_PATH), ANALYTICS_SERVICE(
			ANALYTICS_SERVICE_PATH), LEGAL_SERVICE(LEGAL_SERVICE_PATH);

	private final String path;

	private Service(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
