package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

public class TextRenderContextBuilder {
	private HbaseIndexSchemaImpl schema;

	public TextRenderContextBuilder() {
	}

	public TextRenderContextBuilder setSchema(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
		return this;
	}

	public TextRenderContext build() {
		return new TextRenderContext(schema);
	}
}
