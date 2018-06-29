package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaSerialization;

public class TextRenderContextBuilder {
	private HbaseIndexSchemaSerialization schema;

	public TextRenderContextBuilder() {
	}

	public TextRenderContextBuilder setSchema(HbaseIndexSchemaSerialization schema) {
		this.schema = schema;
		return this;
	}

	public TextRenderContext build() {
		return new TextRenderContext(schema);
	}
}
