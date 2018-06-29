package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaSerialization;

public class HtmlRenderContextBuilder {
	private HbaseIndexSchemaSerialization schema;

	public HtmlRenderContextBuilder() {
	}

	public HtmlRenderContextBuilder setSchema(HbaseIndexSchemaSerialization schema) {
		this.schema = schema;
		return this;
	}

	public HtmlRenderContext build() {
		return new HtmlRenderContext(schema);
	}
}