package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static net.thomas.portfolio.globals.ServiceGlobals.RENDER_SERVICE_PATH;
import static net.thomas.portfolio.globals.ServiceGlobals.USAGE_DATA_SERVICE_PATH;

public enum Service implements ServiceEndpoint {
	HBASE_INDEXING_SERVICE(HBASE_INDEXING_SERVICE_PATH), RENDER_SERVICE(RENDER_SERVICE_PATH), USAGE_DATA_SERVICE(USAGE_DATA_SERVICE_PATH);

	private final String path;

	private Service(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
