package net.thomas.portfolio.graphql.fetchers.conversion;

import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public abstract class FormattedTimestampDataFetcher extends ModelDataFetcher<String> {

	private final DateConverter dateFormatter;

	public FormattedTimestampDataFetcher(Adaptors adaptor) {
		super(adaptor);
		dateFormatter = adaptors.getDateConverter();
	}

	protected String formatTimestamp(String format, long timestamp) {
		if ("dateOnly".equals(format)) {
			return dateFormatter.formatDateTimestamp(timestamp);
		} else {
			return dateFormatter.formatTimestamp(timestamp);
		}
	}
}