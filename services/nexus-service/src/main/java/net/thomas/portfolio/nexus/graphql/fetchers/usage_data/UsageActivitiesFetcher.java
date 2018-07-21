package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.AFTER_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.BEFORE_DATE;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.LIMIT;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.OFFSET;
import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.extractFirstThatIsPresent;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class UsageActivitiesFetcher extends ModelDataFetcher<List<UsageActivity>> {

	public UsageActivitiesFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public List<UsageActivity> get(DataFetchingEnvironment environment) {
		final Bounds bounds = extractBounds(environment);
		return adaptors.fetchUsageActivities(getId(environment), bounds)
			.getActivities();
	}

	private Bounds extractBounds(DataFetchingEnvironment environment) {
		final Integer offset = OFFSET.extractFrom(environment);
		final Integer limit = LIMIT.extractFrom(environment);
		final Long after = extractFirstThatIsPresent(environment, AFTER, AFTER_DATE);
		final Long before = extractFirstThatIsPresent(environment, BEFORE, BEFORE_DATE);
		return new Bounds(offset, limit, after, before);
	}
}