package net.thomas.portfolio.enums;

import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_TEXT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_ENTITY_ROOT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_SELECTOR_ROOT_PATH;

import net.thomas.portfolio.services.ServiceEndpoint;

public enum RenderServiceEndpoint implements ServiceEndpoint {
	RENDER_ENTITY_ROOT(RENDER_ENTITY_ROOT_PATH),
	RENDER_SELECTOR_ROOT(RENDER_SELECTOR_ROOT_PATH),
	AS_SIMPLE_REPRESENTATION(AS_SIMPLE_REPRESENTATION_PATH),
	AS_TEXT(AS_TEXT_PATH),
	AS_HTML(AS_HTML_PATH);
	private final String path;

	private RenderServiceEndpoint(String path) {
		this.path = path;
	}

	@Override
	public String getContextPath() {
		return path;
	}
}