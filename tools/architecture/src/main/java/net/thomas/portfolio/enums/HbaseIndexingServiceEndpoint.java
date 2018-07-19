package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.DOCUMENTS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.ENTITIES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTORS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.STATISTICS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SUGGESTIONS_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum HbaseIndexingServiceEndpoint implements ServiceEndpoint {
	SCHEMA(SCHEMA_PATH), SELECTORS(SELECTORS_PATH), DOCUMENTS(DOCUMENTS_PATH), ENTITIES(ENTITIES_PATH), SUGGESTIONS(SUGGESTIONS_PATH), INVERTED_INDEX(
			INVERTED_INDEX_PATH), STATISTICS(STATISTICS_PATH), REFERENCES(REFERENCES_PATH);
	private final String path;

	private HbaseIndexingServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getContextPath() {
		return path;
	}
}