package net.thomas.portfolio.render.format.html;

import net.thomas.portfolio.render.common.context.RenderContext;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public interface RenderControl {

	String render(DataType datatype, RenderContext context);
}
