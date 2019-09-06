package net.thomas.portfolio.service_commons.adaptors.impl;

import static org.springframework.http.HttpMethod.GET;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@EnableCircuitBreaker
public class AnalyticsAdaptorImpl implements PortfolioInfrastructureAware, AnalyticsAdaptor {
	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;

	@Override
	public void initialize(UrlFactory urlFactory, HttpRestClient client) {
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public AnalyticalKnowledge getKnowledge(DataTypeId selectorId) {
		final String url = urlLibrary.analytics.knowledge(selectorId);
		return client.loadUrlAsObject(url, GET, AnalyticalKnowledge.class);
	}
}