package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import java.util.List;
import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class UsageActivitiesFetcher extends ModelDataFetcher<List<UsageActivity>> {

	private final DateConverter dateFormatter;

	public UsageActivitiesFetcher(Adaptors adaptors) {
		super(adaptors/* , 50 */);
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public List<UsageActivity> _get(DataFetchingEnvironment environment) {
		final Document document = (Document) extractOrFetchDataType(environment);
		final Bounds bounds = extractBounds(environment.getArguments());
		return adaptors.fetchUsageActivity(document.getId(), bounds);
	}

	private Bounds extractBounds(Map<String, Object> arguments) {
		final Integer offset = (Integer) arguments.get("offset");
		final Integer limit = (Integer) arguments.get("limit");
		final Long after = determineAfter(arguments);
		final Long before = determineBefore(arguments);
		return new Bounds(offset, limit, after, before);
	}

	private Long determineAfter(Map<String, Object> arguments) {
		Long after = (Long) arguments.get("after");
		if (after == null && arguments.get("afterDate") != null) {
			after = dateFormatter.parseTimestamp((String) arguments.get("afterDate"));
		}
		return after;
	}

	private Long determineBefore(Map<String, Object> arguments) {
		Long before = (Long) arguments.get("before");
		if (before == null && arguments.get("beforeDate") != null) {
			before = dateFormatter.parseTimestamp((String) arguments.get("beforeDate"));
		}
		return before;
	}

}