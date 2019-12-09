package net.thomas.portfolio.service_commons.adaptors.impl;

import static net.thomas.portfolio.service_commons.hateoas.PortfolioHateoasWrappingHelper.unwrap;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

import net.thomas.portfolio.service_commons.adaptors.specific.UsageAdaptor;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.PortfolioInfrastructureAware;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class UsageAdaptorImpl implements PortfolioInfrastructureAware, UsageAdaptor {
	private static final String STORE_USAGE_ACTIVITY = "storeUsageActivity";
	private static final String FETCH_USAGE_ACTIVITIES = "fetchUsageActivities";
	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient client;

	@Override
	public void initialize(PortfolioUrlLibrary urlLibrary, HttpRestClient client) {
		this.urlLibrary = urlLibrary;
		this.client = client;
	}

	@Override
	@SentinelResource(value = STORE_USAGE_ACTIVITY)
	public UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity) {
		final ParameterizedTypeReference<Resource<UsageActivity>> responceType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.documents().usageActivities(documentId, activity);
		return unwrap(client.loadUrlAsObject(url, POST, responceType));
	}

	@Override
	@SentinelResource(value = FETCH_USAGE_ACTIVITIES)
	public UsageActivities fetchUsageActivities(DataTypeId documentId, Bounds bounds) {
		final ParameterizedTypeReference<Resource<UsageActivities>> responceType = new ParameterizedTypeReference<>() {
		};
		final String url = urlLibrary.documents().usageActivities(documentId, bounds);
		return unwrap(client.loadUrlAsObject(url, GET, responceType));
	}
}