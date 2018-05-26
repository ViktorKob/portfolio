package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.UsageDataServiceGlobals.FETCH_USAGE_ACTIVITY_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.STORE_USAGE_ACTIVITY_PATH;

import net.thomas.portfolio.entities.ServiceEndpoint;

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
