package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;

public class HtmlRenderContextBuilder {
	private HBaseIndexSchemaSerialization schema;

	public HtmlRenderContextBuilder() {
	}

	public HtmlRenderContextBuilder setSchema(HBaseIndexSchemaSerialization schema) {
		this.schema = schema;
		return this;
	}

	public HtmlRenderContext build() {
		return new HtmlRenderContext(schema);
	}
}