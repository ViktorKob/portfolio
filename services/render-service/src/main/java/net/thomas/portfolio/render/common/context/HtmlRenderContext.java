package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;

public class HtmlRenderContext {
	private final HbaseIndexSchemaImpl schema;

	public HtmlRenderContext(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
	}

	public HbaseIndexSchemaImpl getSchema() {
		return schema;
	}
}