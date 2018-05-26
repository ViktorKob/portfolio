package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.UsageDataServiceGlobals.FETCH_USAGE_ACTIVITY_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.STORE_USAGE_ACTIVITY_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum UsageDataServiceEndpoint implements ServiceEndpoint {
	STORE_USAGE_ACTIVITY(FETCH_USAGE_ACTIVITY_PATH), FETCH_USAGE_ACTIVITY(STORE_USAGE_ACTIVITY_PATH);
	private final String path;

	private UsageDataServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
