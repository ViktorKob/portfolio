package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

public class HtmlRenderContextBuilder {
	private HbaseIndexSchemaImpl schema;

	public HtmlRenderContextBuilder() {
	}

	public HtmlRenderContextBuilder setSchema(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
		return this;
	}

	public HtmlRenderContext build() {
		return new HtmlRenderContext(schema);
	}
}