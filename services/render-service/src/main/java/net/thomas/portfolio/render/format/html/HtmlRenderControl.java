package net.thomas.portfolio.render.format.html;

import net.thomas.portfolio.render.common.Renderer;
import net.thomas.portfolio.render.common.context.HtmlRenderContext;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class HtmlRenderControl implements Renderer<String, HtmlRenderContext> {

	public HtmlRenderControl() {
	}

	@Override
	public String render(DataType datatype, HtmlRenderContext context) {
		return "<B>Prending implementation</B>";
	}
}
