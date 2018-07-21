package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.enums.AnalyticsServiceEndpoint.ANALYTICS_BASE;
import static net.thomas.portfolio.enums.AnalyticsServiceEndpoint.LOOKUP_KNOWLEDGE;
import static net.thomas.portfolio.service_commons.network.ServiceEndpointBuilder.asEndpoint;
import static net.thomas.portfolio.services.Service.ANALYTICS_SERVICE;
import static org.springframework.http.HttpMethod.GET;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.AnalyticsAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

@EnableCircuitBreaker
public class AnalyticsAdaptorImpl implements HttpRestClientInitializable, AnalyticsAdaptor {
	private HttpRestClient client;

	@Override
	public void initialize(HttpRestClient client) {
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public AnalyticalKnowledge getKnowledge(DataTypeId selectorId) {
		return client.loadUrlAsObject(ANALYTICS_SERVICE, asEndpoint(ANALYTICS_BASE, selectorId, LOOKUP_KNOWLEDGE), GET, AnalyticalKnowledge.class);
	}
}