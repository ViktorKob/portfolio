package net.thomas.common.services.enums;

import static net.thomas.common.services.globals.RenderServiceGlobals.RENDER_DOCUMENT_LIST_PATH;
import static net.thomas.common.services.globals.RenderServiceGlobals.SHOW_DOCUMENT_PATH;

public enum RenderServiceEndpoint implements ServiceEndpoint {
	RENDER_DOCUMENT_LIST(RENDER_DOCUMENT_LIST_PATH), SHOW_DOCUMENT(SHOW_DOCUMENT_PATH);
	private final String path;

	private RenderServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
