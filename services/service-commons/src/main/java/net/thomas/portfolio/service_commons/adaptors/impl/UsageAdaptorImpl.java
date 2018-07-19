package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.USAGE_ACTIVITIES;
import static net.thomas.portfolio.services.Service.USAGE_DATA_SERVICE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import net.thomas.portfolio.service_commons.adaptors.specific.UsageAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.HttpRestClientInitializable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

@EnableCircuitBreaker
public class UsageAdaptorImpl implements HttpRestClientInitializable, UsageAdaptor {
	private static final ParameterizedTypeReference<List<UsageActivity>> USAGE_ACTIVITY_ITEMS_TYPE_REFERENCE = new ParameterizedTypeReference<List<UsageActivity>>() {
	};
	private HttpRestClient client;

	@Override
	public void initialize(HttpRestClient client) {
		this.client = client;
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3") })
	public UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, () -> {
			return USAGE_ACTIVITIES.getContextPath() + "/" + documentId.type + "/" + documentId.uid;
		}, POST, UsageActivity.class, activity);
	}

	@Override
	@HystrixCommand(commandProperties = { @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "3"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000") })
	public List<UsageActivity> fetchUsageActivities(DataTypeId documentId, Bounds bounds) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, () -> {
			return USAGE_ACTIVITIES.getContextPath() + "/" + documentId.type + "/" + documentId.uid;
		}, GET, USAGE_ACTIVITY_ITEMS_TYPE_REFERENCE, bounds);
	}
}