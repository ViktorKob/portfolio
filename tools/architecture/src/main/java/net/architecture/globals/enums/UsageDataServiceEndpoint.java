package net.architecture.globals.enums;

import static net.architecture.globals.globals.UsageDataServiceGlobals.FETCH_USAGE_ACTIVITY_PATH;
import static net.architecture.globals.globals.UsageDataServiceGlobals.STORE_USAGE_ACTIVITY_PATH;

public enum UsageDataServiceEndpoint implements ServiceEndpoint {
	RENDER_DOCUMENT_LIST(FETCH_USAGE_ACTIVITY_PATH), SHOW_DOCUMENT(STORE_USAGE_ACTIVITY_PATH);
	private final String path;

	private UsageDataServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
