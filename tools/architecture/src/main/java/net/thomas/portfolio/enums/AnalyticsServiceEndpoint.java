package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_PATH;
import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_ROOT_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum AnalyticsServiceEndpoint implements ServiceEndpoint {
	ANALYTICS_BASE(LOOKUP_KNOWLEDGE_ROOT_PATH), LOOKUP_KNOWLEDGE(LOOKUP_KNOWLEDGE_PATH);
	private final String path;

	private AnalyticsServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getContextPath() {
		return path;
	}
}
