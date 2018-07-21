package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.UsageDataServiceGlobals.USAGE_ACTIVITIES_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.USAGE_ACTIVITIES_ROOT_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum UsageDataServiceEndpoint implements ServiceEndpoint {
	USAGE_ACTIVITIES_ROOT(USAGE_ACTIVITIES_ROOT_PATH), USAGE_ACTIVITIES(USAGE_ACTIVITIES_PATH);
	private final String path;

	private UsageDataServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getContextPath() {
		return path;
	}
}
