package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOG_INVERTED_INDEX_LOOKUP_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOG_STATISTICS_LOOKUP_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGALITY_OF_INVERTED_INDEX_QUERY_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGALITY_OF_STATISTICS_LOOKUP_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum LegalServiceEndpoint implements ServiceEndpoint {
	AUDIT_LOG_INVERTED_INDEX_LOOKUP(AUDIT_LOG_INVERTED_INDEX_LOOKUP_PATH),
	AUDIT_LOG_STATISTICS_LOOKUP(AUDIT_LOG_STATISTICS_LOOKUP_PATH),
	LEGALITY_OF_INVERTED_INDEX_QUERY(LEGALITY_OF_INVERTED_INDEX_QUERY_PATH),
	LEGALITY_OF_STATISTICS_LOOKUP(LEGALITY_OF_STATISTICS_LOOKUP_PATH);
	private final String path;

	private LegalServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
