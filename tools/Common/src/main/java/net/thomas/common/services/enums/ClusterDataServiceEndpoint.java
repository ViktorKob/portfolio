package net.thomas.common.services.enums;

import static net.thomas.common.services.globals.ClusterDataServiceGlobals.FETCH_DOCUMENT_PATH;
import static net.thomas.common.services.globals.ClusterDataServiceGlobals.SELECTOR_LOOKUP_PATH;

public enum ClusterDataServiceEndpoint implements ServiceEndpoint {
	SELECTOR_LOOKUP(SELECTOR_LOOKUP_PATH), FETCH_DOCUMENT(FETCH_DOCUMENT_PATH);
	private final String path;

	private ClusterDataServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
