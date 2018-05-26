package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.shared_objects.adaptors.HbaseIndexModelAdaptor;

public class SimpleRepresentationRenderContextBuilder {
	private HbaseIndexModelAdaptor adaptor;

	public SimpleRepresentationRenderContextBuilder() {
	}

	public SimpleRepresentationRenderContextBuilder setHbaseModelAdaptor(HbaseIndexModelAdaptor adaptor) {
		this.adaptor = adaptor;
		return this;
	}

	public SimpleRepresentationRenderContext build() {
		return new SimpleRepresentationRenderContext(adaptor);
	}
}