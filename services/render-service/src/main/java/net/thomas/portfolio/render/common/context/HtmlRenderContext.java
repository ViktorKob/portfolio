package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;

public class HtmlRenderContext {
	private final HBaseIndexSchemaSerialization schema;

	public HtmlRenderContext(HBaseIndexSchemaSerialization schema) {
		this.schema = schema;
	}

	public HBaseIndexSchemaSerialization getSchema() {
		return schema;
	}
}