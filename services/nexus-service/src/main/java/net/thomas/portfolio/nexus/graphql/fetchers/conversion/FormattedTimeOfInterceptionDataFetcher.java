package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.data_proxies.DocumentProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class FormattedTimeOfInterceptionDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfInterceptionDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final long timestamp = ((DocumentProxy<?>) getProxy(environment)).getTimeOfInterception();
		return formatTimestampAsIec8601(environment, timestamp);
	}
}