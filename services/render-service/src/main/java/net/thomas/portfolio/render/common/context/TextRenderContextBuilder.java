package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;

public class TextRenderContextBuilder {
	private HBaseIndexSchemaSerialization schema;

	public TextRenderContextBuilder() {
	}

	public TextRenderContextBuilder setSchema(HBaseIndexSchemaSerialization schema) {
		this.schema = schema;
		return this;
	}

	public TextRenderContext build() {
		return new TextRenderContext(schema);
	}
}
