package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;

public class TextRenderContext {
	private final HBaseIndexSchemaSerialization schema;

	public TextRenderContext(HBaseIndexSchemaSerialization schema) {
		this.schema = schema;
	}

	public HBaseIndexSchemaSerialization getSchema() {
		return schema;
	}
}
