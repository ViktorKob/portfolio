package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_DATA_TYPE_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SELECTOR_SUGGESTIONS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_STATISTICS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.LOOKUP_SELECTOR_IN_INVERTED_INDEX_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum HbaseIndexingServiceEndpoint implements ServiceEndpoint {
	GET_SCHEMA(GET_SCHEMA_PATH), GET_SAMPLES(GET_SAMPLES_PATH), GET_SELECTOR_SUGGESTIONS(GET_SELECTOR_SUGGESTIONS_PATH), GET_DATA_TYPE(
			GET_DATA_TYPE_PATH), LOOKUP_SELECTOR_IN_INVERTED_INDEX(LOOKUP_SELECTOR_IN_INVERTED_INDEX_PATH), GET_STATISTICS(GET_STATISTICS_PATH), GET_REFERENCES(GET_REFERENCES_PATH);
	private final String path;

	private HbaseIndexingServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}