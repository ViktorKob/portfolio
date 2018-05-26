package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_AS_TEXT_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum RenderServiceEndpoint implements ServiceEndpoint {
	RENDER_AS_SIMPLE_REPRESENTATION(RENDER_AS_SIMPLE_REPRESENTATION_PATH), RENDER_AS_TEXT(RENDER_AS_TEXT_PATH), RENDER_AS_HTML(RENDER_AS_HTML_PATH);
	private final String path;

	private RenderServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return path;
	}
}
