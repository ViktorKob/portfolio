package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_PRIOR_KNOWLEDGE_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum AnalyticsServiceEndpoint implements ServiceEndpoint {
	LOOKUP_PRIOR_KNOWLEDGE(LOOKUP_PRIOR_KNOWLEDGE_PATH);
	private final String path;

	private AnalyticsServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
