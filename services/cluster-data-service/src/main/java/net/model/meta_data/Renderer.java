package net.model.meta_data;

import net.model.DataType;

public interface Renderer<RENDER_TYPE> {
	RENDER_TYPE render(DataType element);
}
