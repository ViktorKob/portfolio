package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.conversion.FormattedTimestampDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityItem;

public class FormattedTimeOfActivityFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfActivityFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final UsageActivityItem item = (UsageActivityItem) environment.getSource();
		return formatTimestampAsIec8601(environment, item.timeOfActivity);
	}
}