package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;

public interface Renderer<RENDER_TYPE> {
	RENDER_TYPE render(Datatype element);
}
