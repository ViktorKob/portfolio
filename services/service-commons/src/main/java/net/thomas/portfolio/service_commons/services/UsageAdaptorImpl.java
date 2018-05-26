package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.common.services.ParameterGroup.SimpleParameterGroup.asGroup;
import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.FETCH_USAGE_ACTIVITY;
import static net.thomas.portfolio.enums.UsageDataServiceEndpoint.STORE_USAGE_ACTIVITY;
import static net.thomas.portfolio.services.Service.USAGE_DATA_SERVICE;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import net.thomas.portfolio.common.services.PreSerializedParameter;
import net.thomas.portfolio.shared_objects.adaptors.UsageAdaptor;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityItem;

public class UsageAdaptorImpl implements UsageAdaptor {
	private static final ParameterizedTypeReference<List<UsageActivityItem>> USAGE_ACTIVITY_ITEM_TYPE_REFERENCE = new ParameterizedTypeReference<List<UsageActivityItem>>() {
	};
	private final HttpRestClient client;

	public UsageAdaptorImpl(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public boolean storeUsageActivity(DataTypeId documentId, UsageActivityItem item) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, STORE_USAGE_ACTIVITY, Boolean.class, documentId, item);
	}

	@Override
	public List<UsageActivityItem> fetchUsageActivity(DataTypeId documentId, Integer offset, Integer limit) {
		return client.loadUrlAsObject(USAGE_DATA_SERVICE, FETCH_USAGE_ACTIVITY, USAGE_ACTIVITY_ITEM_TYPE_REFERENCE, documentId,
				asGroup(new PreSerializedParameter("offset", offset), new PreSerializedParameter("limit", limit)));
	}
}