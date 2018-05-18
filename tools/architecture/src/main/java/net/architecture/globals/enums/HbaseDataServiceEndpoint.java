package net.architecture.globals.enums;

import static net.architecture.globals.globals.HbaseDataServiceGlobals.FETCH_DOCUMENT_PATH;
import static net.architecture.globals.globals.HbaseDataServiceGlobals.SELECTOR_LOOKUP_PATH;

public enum HbaseDataServiceEndpoint implements ServiceEndpoint {
	SELECTOR_LOOKUP(SELECTOR_LOOKUP_PATH), FETCH_DOCUMENT(FETCH_DOCUMENT_PATH);
	private final String path;

	private HbaseDataServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
