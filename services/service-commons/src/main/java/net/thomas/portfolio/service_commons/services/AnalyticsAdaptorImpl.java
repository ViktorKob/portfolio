package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.AnalyticsServiceEndpoint.LOOKUP_KNOWLEDGE;
import static net.thomas.portfolio.services.Service.ANALYTICS_SERVICE;
import static org.springframework.http.HttpMethod.GET;

import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class AnalyticsAdaptorImpl implements AnalyticsAdaptor {

	private HttpRestClient client;

	public void initialize(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public AnalyticalKnowledge getKnowledge(DataTypeId selectorId) {
		return client.loadUrlAsObject(ANALYTICS_SERVICE, () -> {
			return LOOKUP_KNOWLEDGE.getPath() + "/" + selectorId.type + "/" + selectorId.uid;
		}, GET, AnalyticalKnowledge.class);
	}
}