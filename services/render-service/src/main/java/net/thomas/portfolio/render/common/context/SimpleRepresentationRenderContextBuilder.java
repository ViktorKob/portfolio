package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;

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