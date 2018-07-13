package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.conversion.FormattedTimestampDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class FormattedTimeOfActivityFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfActivityFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final UsageActivity item = environment.getSource();
		return formatTimestampAsIec8601(environment, item.timeOfActivity);
	}
}