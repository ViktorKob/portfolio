package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOG_INVERTED_INDEX_LOOKUP_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOG_STATISTICS_LOOKUP_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.CHECK_LEGALITY_OF_QUERY_ON_SELECTOR_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum LegalServiceEndpoint implements ServiceEndpoint {
	AUDIT_LOG_INVERTED_INDEX_LOOKUP(AUDIT_LOG_INVERTED_INDEX_LOOKUP_PATH), AUDIT_LOG_STATISTICS_LOOKUP(
			AUDIT_LOG_STATISTICS_LOOKUP_PATH), CHECK_LEGALITY_OF_QUERY_ON_SELECTOR(CHECK_LEGALITY_OF_QUERY_ON_SELECTOR_PATH);
	private final String path;

	private LegalServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
