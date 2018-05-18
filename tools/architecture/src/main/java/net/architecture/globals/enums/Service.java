package net.architecture.globals.enums;

import static net.architecture.globals.globals.ServiceGlobals.HBASE_DATA_SERVICE_PATH;
import static net.architecture.globals.globals.ServiceGlobals.USAGE_DATA_SERVICE_PATH;

public enum Service {
	CLUSTER_DATA_SERVICE(HBASE_DATA_SERVICE_PATH), USAGE_DATA_SERVICE(USAGE_DATA_SERVICE_PATH);

	private final String path;

	private Service(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
