package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.enums.RenderServiceEndpoint.AS_HTML;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.AS_SIMPLE_REPRESENTATION;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.AS_TEXT;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_ENTITY_ROOT;
import static net.thomas.portfolio.enums.RenderServiceEndpoint.RENDER_SELECTOR_ROOT;
import static net.thomas.portfolio.service_commons.network.ServiceEndpointBuilder.asEndpoint;
import static net.thomas.portfolio.services.Service.RENDER_SERVICE;
import static org.springframework.http.HttpMethod.GET;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.RenderingAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@EnableCircuitBreaker
public class RenderingAdaptorImpl implements HttpRestClientInitializable, RenderingAdaptor {

	private HttpRestClient client;

	@Override
	public void initialize(HttpRestClient client) {
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public String renderAsSimpleRepresentation(DataTypeId selectorId) {
		return client.loadUrlAsObject(RENDER_SERVICE, asEndpoint(RENDER_SELECTOR_ROOT, selectorId, AS_SIMPLE_REPRESENTATION), GET, String.class);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public String renderAsText(DataTypeId id) {
		return client.loadUrlAsObject(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_TEXT), GET, String.class);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public String renderAsHtml(DataTypeId id) {
		return client.loadUrlAsObject(RENDER_SERVICE, asEndpoint(RENDER_ENTITY_ROOT, id, AS_HTML), GET, String.class);
	}
}