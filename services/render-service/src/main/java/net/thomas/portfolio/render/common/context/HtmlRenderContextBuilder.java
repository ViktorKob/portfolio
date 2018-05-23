package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;

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