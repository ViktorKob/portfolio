package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentProxy;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;

public class FormattedTimeOfEventDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfEventDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final Long timestamp = ((DocumentProxy<?>) getProxy(environment)).getTimeOfEvent();
		return timestamp == null ? null : formatTimestampAsIec8601(environment, timestamp);
	}
}