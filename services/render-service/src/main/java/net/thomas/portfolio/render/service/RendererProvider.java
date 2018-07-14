package net.thomas.portfolio.render.service;

import net.thomas.portfolio.render.common.context.HtmlRenderContext;
import net.thomas.portfolio.render.common.context.SimpleRepresentationRenderContext;
import net.thomas.portfolio.render.common.context.TextRenderContext;
import net.thomas.portfolio.render.format.html.HtmlRenderControl;
import net.thomas.portfolio.render.format.simple_rep.HbaseIndexingModelSimpleRepresentationRendererLibrary;
import net.thomas.portfolio.render.format.text.HbaseIndexingModelTextRendererLibrary;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class RendererProvider {
	private final HbaseIndexingModelSimpleRepresentationRendererLibrary simpleRepRenderer;
	private final HbaseIndexingModelTextRendererLibrary textRenderer;
	private final HtmlRenderControl htmlRenderer;

	public RendererProvider() {
		simpleRepRenderer = new HbaseIndexingModelSimpleRepresentationRendererLibrary();
		textRenderer = new HbaseIndexingModelTextRendererLibrary();
		htmlRenderer = new HtmlRenderControl();
	}

	public String renderAsSimpleRep(DataType entity, SimpleRepresentationRenderContext context) {
		return simpleRepRenderer.render(entity, context);
	}

	public String renderAsText(DataType entity, TextRenderContext context) {
		return textRenderer.render(entity, context);
	}

	public String renderAsHtml(DataType entity, HtmlRenderContext context) {
		return htmlRenderer.render(entity, context);
	}
}