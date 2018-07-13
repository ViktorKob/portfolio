package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.FORMATTED_TIME_OF_ACTIVITY;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.TIME_OF_ACTIVITY;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USAGE_ACTIVITY_TYPE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.USER;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.extractFirstThatIsPresent;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class UsageActivityMutation extends ModelDataFetcher<UsageActivity> {

	public UsageActivityMutation(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public UsageActivity get(DataFetchingEnvironment environment) {
		final DataTypeId documentId = getId(environment);
		final UsageActivityType activityType = USAGE_ACTIVITY_TYPE.extractFrom(environment);
		final Long timeOfActivity = extractFirstThatIsPresent(environment, TIME_OF_ACTIVITY, FORMATTED_TIME_OF_ACTIVITY);
		final UsageActivity activity = new UsageActivity(USER.extractFrom(environment), activityType, timeOfActivity);
		return adaptors.storeUsageActivity(documentId, activity);
	}
}