package net.thomas.portfolio.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class FormattedTimeOfInterceptionDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfInterceptionDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Document document = environment.getSource();
		if (environment.getArgument("format") != null) {
			return formatTimestamp(environment.getArgument("format")
				.toString(), document.getTimeOfInterception());
		} else {
			return formatTimestamp(null, document.getTimeOfInterception());
		}
	}
}