package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_DATA_TYPE_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_STATISTICS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.INVERTED_INDEX_LOOKUP_PATH;

import net.thomas.portfolio.entities.ServiceEndpoint;

public enum HbaseDataServiceEndpoint implements ServiceEndpoint {
	GET_SCHEMA(GET_SCHEMA_PATH), GET_SAMPLES(GET_SAMPLES_PATH), GET_DATA_TYPE(GET_DATA_TYPE_PATH), INVERTED_INDEX_LOOKUP(
			INVERTED_INDEX_LOOKUP_PATH), GET_STATISTICS(GET_STATISTICS_PATH), GET_REFERENCES(GET_REFERENCES_PATH);
	private final String path;

	private HbaseDataServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}