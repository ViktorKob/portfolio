package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.UsageAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.UrlFactory;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

@EnableCircuitBreaker
public class UsageAdaptorImpl implements PortfolioInfrastructureAware, UsageAdaptor {
	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;

	@Override
	public void initialize(final UrlFactory urlFactory, final HttpRestClient client) {
		urlLibrary = new PortfolioUrlLibrary(urlFactory);
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity) {
		final ParameterizedTypeReference<Resource<UsageActivity>> responceType = new ParameterizedTypeReference<Resource<UsageActivity>>() {
		};
		final String url = urlLibrary.usageData.usageActivities(documentId, activity);
		return unwrap(client.loadUrlAsObject(url, POST, responceType));
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
	public UsageActivities fetchUsageActivities(DataTypeId documentId, Bounds bounds) {
		final ParameterizedTypeReference<Resource<UsageActivities>> responceType = new ParameterizedTypeReference<Resource<UsageActivities>>() {
		};
		final String url = urlLibrary.usageData.usageActivities(documentId, bounds);
		return unwrap(client.loadUrlAsObject(url, GET, responceType));
	}
}