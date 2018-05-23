package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;

public class SimpleRepresentationRenderContext {
	private final HbaseIndexSchemaImpl schema;

	public SimpleRepresentationRenderContext(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
	}

	public HbaseIndexSchemaImpl getSchema() {
		return schema;
	}
}