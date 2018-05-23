package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.hbase_index.fake.HbaseIndexSchemaImpl;

public class SimpleRepresentationRenderContextBuilder {
	private HbaseIndexSchemaImpl schema;

	public SimpleRepresentationRenderContextBuilder() {
	}

	public SimpleRepresentationRenderContextBuilder setSchema(HbaseIndexSchemaImpl schema) {
		this.schema = schema;
		return this;
	}

	public SimpleRepresentationRenderContext build() {
		return new SimpleRepresentationRenderContext(schema);
	}
}