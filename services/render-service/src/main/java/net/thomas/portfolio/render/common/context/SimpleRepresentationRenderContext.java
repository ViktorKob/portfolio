package net.thomas.portfolio.render.common.context;

import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;

public class SimpleRepresentationRenderContext implements RenderContext {
	private final HbaseIndexModelAdaptor adaptor;

	public SimpleRepresentationRenderContext(HbaseIndexModelAdaptor adaptor) {
		this.adaptor = adaptor;
	}

	public HbaseIndexModelAdaptor getHbaseModelAdaptor() {
		return adaptor;
	}
}