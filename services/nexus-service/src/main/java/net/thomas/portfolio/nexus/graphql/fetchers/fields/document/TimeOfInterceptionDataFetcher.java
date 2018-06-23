package net.thomas.portfolio.nexus.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DocumentProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class TimeOfInterceptionDataFetcher extends ModelDataFetcher<Long> {

	public TimeOfInterceptionDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Long get(DataFetchingEnvironment environment) {
		return ((DocumentProxy<?>) getProxy(environment)).getTimeOfInterception();
	}
}