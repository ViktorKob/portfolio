package net.thomas.portfolio.render.common;

import net.thomas.portfolio.render.common.context.RenderContext;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public interface Renderer<RENDER_TYPE, RENDER_CONTEXT_TYPE extends RenderContext> {
	RENDER_TYPE render(DataType element, RENDER_CONTEXT_TYPE context);
}