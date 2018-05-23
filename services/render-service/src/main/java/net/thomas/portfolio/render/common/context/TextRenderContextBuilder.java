package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;

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
