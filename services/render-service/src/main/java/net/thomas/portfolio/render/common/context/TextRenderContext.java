package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

public class TextRenderContext implements RenderContext {
	private final HbaseIndexSchemaImpl schema;

	public TextRenderContext(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
	}

	public HbaseIndexSchemaImpl getSchema() {
		return schema;
	}
}
