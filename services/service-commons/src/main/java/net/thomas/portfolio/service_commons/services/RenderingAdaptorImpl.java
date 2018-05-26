package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_AS_HTML;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_AS_SIMPLE_REPRESENTATION;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_AS_TEXT;
import static net.thomas.portfolio.services.Service.RENDER_SERVICE;

import net.thomas.portfolio.shared_objects.adaptors.RenderingAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class RenderingAdaptorImpl implements RenderingAdaptor {

	private final HttpRestClient client;

	public RenderingAdaptorImpl(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public String renderAsSimpleRepresentation(DataTypeId selectorId) {
		return client.loadUrlAsObject(RENDER_SERVICE, RENDER_AS_SIMPLE_REPRESENTATION, String.class, selectorId);
	}

	@Override
	public String renderAsText(DataTypeId id) {
		return client.loadUrlAsObject(RENDER_SERVICE, RENDER_AS_TEXT, String.class, id);
	}

	@Override
	public String renderAsHtml(DataTypeId id) {
		return client.loadUrlAsObject(RENDER_SERVICE, RENDER_AS_HTML, String.class, id);
	}
}