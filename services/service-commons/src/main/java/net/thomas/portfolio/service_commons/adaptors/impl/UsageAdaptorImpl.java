package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.USAGE_ACTIVITIES;
import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.USAGE_ACTIVITIES_ROOT;
import static net.thomas.portfolio.service_commons.network.ServiceEndpointBuilder.asEndpoint;
import static net.thomas.portfolio.services.Service.USAGE_DATA_SERVICE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.UsageAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

@EnableCircuitBreaker
public class UsageAdaptorImpl implements HttpRestClientInitializable, UsageAdaptor {
	private HttpRestClient client;

	@Override
	public void initialize(HttpRestClient client) {
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, asEndpoint(USAGE_ACTIVITIES_ROOT, documentId, USAGE_ACTIVITIES), POST, UsageActivity.class, activity);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
	public UsageActivities fetchUsageActivities(DataTypeId documentId, Bounds bounds) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, asEndpoint(USAGE_ACTIVITIES_ROOT, documentId, USAGE_ACTIVITIES), GET, UsageActivities.class, bounds);
	}
}