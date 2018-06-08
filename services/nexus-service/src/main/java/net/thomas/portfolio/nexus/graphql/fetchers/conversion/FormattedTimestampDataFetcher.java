package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;

public abstract class FormattedTimestampDataFetcher extends ModelDataFetcher<String> {

	private final DateConverter dateFormatter;

	public FormattedTimestampDataFetcher(Adaptors adaptor) {
		super(adaptor);
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	protected String formatTimestampAsIec8601(DataFetchingEnvironment environment, long timestamp) {
		if (environment.getArgument("detailLevel") != null) {
			return formatTimestamp(environment.getArgument("detailLevel")
				.toString(), timestamp);
		} else {
			return formatTimestamp((String) null, timestamp);
		}
	}

	protected String formatTimestamp(String detailLevel, long timestamp) {
		if ("dateOnly".equals(detailLevel)) {
			return dateFormatter.formatDateTimestamp(timestamp);
		} else {
			return dateFormatter.formatTimestamp(timestamp);
		}
	}
}