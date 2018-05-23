package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_AS_HTML;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_AS_SIMPLE_REPRESENTATION;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_AS_TEXT;
import static net.thomas.portfolio.enums.Service.RENDER_SERVICE;

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.RenderingAdaptor;

public class RenderingAdaptorImpl implements RenderingAdaptor {

	private final HttpRestClient client;

	public RenderingAdaptorImpl(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public String renderAsSimpleRepresentation(Selector selector) {
		return client.loadUrlAsObject(RENDER_SERVICE, RENDER_AS_SIMPLE_REPRESENTATION, String.class, new PreSerializedParameter("type", selector.getType()),
				new PreSerializedParameter("uid", selector.getUid()));
	}

	@Override
	public String renderAsText(DataType entity) {
		return client.loadUrlAsObject(RENDER_SERVICE, RENDER_AS_TEXT, String.class, new PreSerializedParameter("type", entity.getType()),
				new PreSerializedParameter("uid", entity.getUid()));
	}

	@Override
	public String renderAsHtml(DataType entity) {
		return client.loadUrlAsObject(RENDER_SERVICE, RENDER_AS_HTML, String.class, new PreSerializedParameter("type", entity.getType()),
				new PreSerializedParameter("uid", entity.getUid()));
	}
}