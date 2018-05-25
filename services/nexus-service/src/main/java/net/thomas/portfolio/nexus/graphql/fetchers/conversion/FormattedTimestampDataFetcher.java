package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;

public abstract class FormattedTimestampDataFetcher extends ModelDataFetcher<String> {

	private final DateConverter dateFormatter;

	public FormattedTimestampDataFetcher(Adaptors adaptor) {
		super(adaptor);
		dateFormatter = adaptors.getDateConverter();
	}

	protected String formatTimestamp(DataFetchingEnvironment environment, long timestamp) {
		if (environment.getArgument("format") != null) {
			return formatTimestamp(environment.getArgument("format")
				.toString(), timestamp);
		} else {
			return formatTimestamp((String) null, timestamp);
		}
	}

	protected String formatTimestamp(String format, long timestamp) {
		if ("dateOnly".equals(format)) {
			return dateFormatter.formatDateTimestamp(timestamp);
		} else {
			return dateFormatter.formatTimestamp(timestamp);
		}
	}
}