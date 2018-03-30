package net.thomas.common.services.enums;

import static net.thomas.common.services.globals.ServiceGlobals.CLUSTER_DATA_SERVICE_PATH;
import static net.thomas.common.services.globals.ServiceGlobals.RENDER_SERVICE_PATH;

public enum Service {
	CLUSTER_DATA_SERVICE(CLUSTER_DATA_SERVICE_PATH), RENDER_SERVICE(RENDER_SERVICE_PATH);

	private final String path;

	private Service(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
