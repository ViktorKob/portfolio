package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.USAGE_ACTIVITIES;
import static net.thomas.portfolio.services.Service.USAGE_DATA_SERVICE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import net.thomas.portfolio.shared_objects.adaptors.UsageAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class UsageAdaptorImpl implements UsageAdaptor {
	private static final ParameterizedTypeReference<List<UsageActivity>> USAGE_ACTIVITY_ITEMS_TYPE_REFERENCE = new ParameterizedTypeReference<List<UsageActivity>>() {
	};
	private final HttpRestClient client;

	public UsageAdaptorImpl(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, () -> {
			return USAGE_ACTIVITIES.getPath() + "/" + documentId.type + "/" + documentId.uid;
		}, POST, UsageActivity.class, activity);
	}

	@Override
	public List<UsageActivity> fetchUsageActivity(DataTypeId documentId, Bounds bounds) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, () -> {
			return USAGE_ACTIVITIES.getPath() + "/" + documentId.type + "/" + documentId.uid;
		}, GET, USAGE_ACTIVITY_ITEMS_TYPE_REFERENCE, bounds);
	}
}