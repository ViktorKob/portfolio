package net.thomas.portfolio.render.common;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public interface Renderer<RENDER_TYPE, RENDER_CONTEXT_TYPE> {
	RENDER_TYPE render(DataType element, RENDER_CONTEXT_TYPE context);
}