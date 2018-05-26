package net.thomas.portfolio.service_commons.services;

import static net.thomas.portfolio.enums.AnalyticsServiceEndpoint.LOOKUP_PRIOR_KNOWLEDGE;
import static net.thomas.portfolio.services.Service.ANALYTICS_SERVICE;

import net.thomas.portfolio.shared_objects.adaptors.AnalyticsAdaptor;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class AnalyticsAdaptorImpl implements AnalyticsAdaptor {

	private final HttpRestClient client;

	public AnalyticsAdaptorImpl(HttpRestClient client) {
		this.client = client;
	}

	@Override
	public PriorKnowledge getPriorKnowledge(DataTypeId selectorId) {
		return client.loadUrlAsObject(ANALYTICS_SERVICE, LOOKUP_PRIOR_KNOWLEDGE, PriorKnowledge.class, selectorId);
	}
}