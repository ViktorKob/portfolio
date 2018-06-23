package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import java.util.Map;

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
		final Map<String, Object> arguments = environment.getArguments();
		final UsageActivityType activityType = extractActivityType(arguments);
		final Long timeOfActivity = extractTimeOfActivity(arguments);
		final UsageActivity activity = new UsageActivity((String) arguments.get("user"), activityType, timeOfActivity);
		return adaptors.storeUsageActivity(documentId, activity);
	}

	private UsageActivityType extractActivityType(Map<String, Object> arguments) {
		return UsageActivityType.valueOf((String) arguments.get("activityType"));
	}

	private Long extractTimeOfActivity(Map<String, Object> arguments) {
		Long timeOfActivity = (Long) arguments.get("timeOfActivity");
		if (timeOfActivity == null && arguments.get("parsedTimeOfActivity") != null) {
			timeOfActivity = dateFormatter.parseTimestamp((String) arguments.get("parsedTimeOfActivity"));
		}
		return timeOfActivity;
	}
}