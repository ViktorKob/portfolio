package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class UsageActivityMutation extends ModelDataFetcher<UsageActivity> {

	private final DateConverter dateFormatter;

	public UsageActivityMutation(Adaptors adaptors) {
		super(adaptors);
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public UsageActivity get(DataFetchingEnvironment environment) {
		final DataTypeId documentId = getId(environment);
		final UsageActivityType activityType = extractActivityType(environment);
		final Long timeOfActivity = extractTimeOfActivity(environment);
		final UsageActivity activity = new UsageActivity(environment.getArgument("user"), activityType, timeOfActivity);
		return adaptors.storeUsageActivity(documentId, activity);
	}

	private UsageActivityType extractActivityType(DataFetchingEnvironment environment) {
		return UsageActivityType.valueOf(environment.getArgument("activityType"));
	}

	private Long extractTimeOfActivity(DataFetchingEnvironment environment) {
		Long timeOfActivity = environment.getArgument("timeOfActivity");
		if (timeOfActivity == null && environment.getArgument("parsedTimeOfActivity") != null) {
			timeOfActivity = dateFormatter.parseTimestamp(environment.getArgument("parsedTimeOfActivity"));
		}
		return timeOfActivity;
	}
}