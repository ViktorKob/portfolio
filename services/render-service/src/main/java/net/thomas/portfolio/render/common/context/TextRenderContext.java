package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;

public class TextRenderContext {
	private final HbaseIndexSchemaImpl schema;

	public TextRenderContext(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
	}

	public HbaseIndexSchemaImpl getSchema() {
		return schema;
	}
}
