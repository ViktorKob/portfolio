package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_DATATYPE_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.GET_SCHEMA_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTOR_LOOKUP_PATH;

public enum HbaseDataServiceEndpoint implements ServiceEndpoint {
	GET_SCHEMA(GET_SCHEMA_PATH), GET_SAMPLES(GET_SAMPLES_PATH), GET_DATATYPE(GET_DATATYPE_PATH), SELECTOR_LOOKUP(SELECTOR_LOOKUP_PATH);
	private final String path;

	private HbaseDataServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
