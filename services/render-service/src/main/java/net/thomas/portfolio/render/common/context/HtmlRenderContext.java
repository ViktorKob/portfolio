package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

public class HtmlRenderContext implements RenderContext {
	private final HbaseIndexSchemaImpl schema;

	public HtmlRenderContext(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
	}

	public HbaseIndexSchemaImpl getSchema() {
		return schema;
	}
}