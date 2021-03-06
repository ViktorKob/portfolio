package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
import static org.springframework.http.HttpMethod.GET;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

// @EnableCircuitBreaker
public class AnalyticsAdaptorImpl implements PortfolioInfrastructureAware, AnalyticsAdaptor {
	public static final String GET_KNOWLEDGE = "getKnowledge";

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;

	@Override
	public void initialize(PortfolioUrlLibrary urlLibrary, HttpRestClient client) {
		this.urlLibrary = urlLibrary;
		this.client = client;
	}

	@Override
	@SentinelResource(value = GET_KNOWLEDGE)
	public AnalyticalKnowledge getKnowledge(DataTypeId selectorId) {
		final ParameterizedTypeReference<Resource<AnalyticalKnowledge>> responseType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.selectors().knowledge(selectorId);
		return unwrap(client.loadUrlAsObject(url, GET, responseType));
	}
}