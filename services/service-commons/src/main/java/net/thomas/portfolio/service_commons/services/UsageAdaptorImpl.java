package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.FETCH_USAGE_ACTIVITY;
import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.STORE_USAGE_ACTIVITY;
import static net.thomas.portfolio.services.Service.USAGE_DATA_SERVICE;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import net.thomas.portfolio.shared_objects.adaptors.UsageAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class UsageAdaptorImpl implements UsageAdaptor {
	private static final ParameterizedTypeReference<List<UsageActivity>> USAGE_ACTIVITY_ITEM_TYPE_REFERENCE = new ParameterizedTypeReference<List<UsageActivity>>() {
	};
	private final HttpRestClient client;

	public UsageAdaptorImpl(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public UsageActivity storeUsageActivity(DataTypeId documentId, UsageActivity activity) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, STORE_USAGE_ACTIVITY, UsageActivity.class, documentId, activity);
	}

	@Override
	public List<UsageActivity> fetchUsageActivity(DataTypeId documentId, Bounds bounds) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, FETCH_USAGE_ACTIVITY, USAGE_ACTIVITY_ITEM_TYPE_REFERENCE, documentId, bounds);
	}
}