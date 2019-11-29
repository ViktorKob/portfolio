package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
import static org.springframework.http.HttpMethod.GET;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

import net.thomas.portfolio.service_commons.adaptors.specific.RenderingAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

// @EnableCircuitBreaker
public class RenderingAdaptorImpl implements PortfolioInfrastructureAware, RenderingAdaptor {
	public static final String RENDER_AS_SIMPLE_REPRESENTATION = "renderAsSimpleRepresentation";
	public static final String RENDER_AS_TEXT = "renderAsText";
	public static final String RENDER_AS_HTML = "renderAsHtml";

	private final ParameterizedTypeReference<Resource<String>> STRING_RESOURCE = new ParameterizedTypeReference<>() {
	};

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;

	@Override
	public void initialize(final UrlFactory urlFactory, final HttpRestClient client) {
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
		this.client = client;
	}

	@Override
	@SentinelResource(value = RENDER_AS_SIMPLE_REPRESENTATION)
	public String renderAsSimpleRepresentation(DataTypeId selectorId) {
		final String url = urlLibrary.selectors.render.simpleRepresentation(selectorId);
		return unwrap(client.loadUrlAsObject(url, GET, STRING_RESOURCE));
	}

	@Override
	@SentinelResource(value = RENDER_AS_TEXT)
	public String renderAsText(DataTypeId id) {
		final String url = urlLibrary.entities.render.text(id);
		return unwrap(client.loadUrlAsObject(url, GET, STRING_RESOURCE));
	}

	@Override
	@SentinelResource(value = RENDER_AS_HTML)
	public String renderAsHtml(DataTypeId id) {
		final String url = urlLibrary.entities.render.html(id);
		return unwrap(client.loadUrlAsObject(url, GET, STRING_RESOURCE));
	}
}