package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaSerialization;

public class TextRenderContext implements RenderContext {
	private final HbaseIndexSchemaSerialization schema;

	public TextRenderContext(HbaseIndexSchemaSerialization schema) {
		this.schema = schema;
	}

	public HbaseIndexSchemaSerialization getSchema() {
		return schema;
	}
}
