package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOGGING_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_RULES_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.STATISTICS_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum LegalServiceEndpoint implements ServiceEndpoint {
	LEGAL_ROOT(LEGAL_ROOT_PATH),
	LEGAL_RULES(LEGAL_RULES_PATH),
	AUDIT_LOG(AUDIT_LOGGING_PATH),
	INVERTED_INDEX_QUERY(INVERTED_INDEX_PATH),
	STATISTICS_LOOKUP(STATISTICS_PATH);

	private final String path;

	private LegalServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getContextPath() {
		return path;
	}
}
