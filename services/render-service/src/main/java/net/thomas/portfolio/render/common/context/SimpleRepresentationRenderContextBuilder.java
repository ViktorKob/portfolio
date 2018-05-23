package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HBaseIndexSchemaSerialization;

public class SimpleRepresentationRenderContextBuilder {
	private HBaseIndexSchemaSerialization schema;

	public SimpleRepresentationRenderContextBuilder() {
	}

	public SimpleRepresentationRenderContextBuilder setSchema(HBaseIndexSchemaSerialization schema) {
		this.schema = schema;
		return this;
	}

	public SimpleRepresentationRenderContext build() {
		return new SimpleRepresentationRenderContext(schema);
	}
}