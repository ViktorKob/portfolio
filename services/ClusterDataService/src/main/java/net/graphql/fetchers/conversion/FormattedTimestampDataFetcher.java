package net.graphql.fetchers.conversion;

import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.util.DateConverter;

public abstract class FormattedTimestampDataFetcher extends ModelDataFetcher<String> {

	private final DateConverter dateFormatter;

	public FormattedTimestampDataFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
		dateFormatter = adaptor.getDateConverter();
	}

	protected String formatTimestamp(String format, long timestamp) {
		if ("dateOnly".equals(format)) {
			return dateFormatter.formatDateTimestamp(timestamp);
		} else {
			return dateFormatter.formatTimestamp(timestamp);
		}
	}
}