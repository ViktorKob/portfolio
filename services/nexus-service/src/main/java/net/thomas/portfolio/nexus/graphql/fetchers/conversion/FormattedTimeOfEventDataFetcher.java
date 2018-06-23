package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class FormattedTimeOfEventDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfEventDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final long timestamp = ((DocumentProxy<?>) getProxy(environment)).getTimeOfEvent();
		return formatTimestampAsIec8601(environment, timestamp);
	}
}