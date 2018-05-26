package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.LegalServiceGlobals.CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP_PATH;

import net.thomas.portfolio.entities.ServiceEndpoint;

public enum LegalServiceEndpoint implements ServiceEndpoint {
	CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP(CHECK_LEGALITY_OF_INVERTED_INDEX_LOOKUP_PATH);
	private final String path;

	private LegalServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
