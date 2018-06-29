package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaSerialization;

public class HtmlRenderContext implements RenderContext {
	private final HbaseIndexSchemaSerialization schema;

	public HtmlRenderContext(HbaseIndexSchemaSerialization schema) {
		this.schema = schema;
	}

	public HbaseIndexSchemaSerialization getSchema() {
		return schema;
	}
}