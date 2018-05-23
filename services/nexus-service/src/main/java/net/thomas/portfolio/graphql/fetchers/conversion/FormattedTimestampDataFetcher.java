package net.thomas.portfolio.graphql.fetchers.conversion;

import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.hbase_index.GraphQlUtilities;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public abstract class FormattedTimestampDataFetcher extends ModelDataFetcher<String> {

	private final DateConverter dateFormatter;

	public FormattedTimestampDataFetcher(HbaseModelAdaptor adaptor, GraphQlUtilities utilities) {
		super(adaptor, 0);
		dateFormatter = utilities.getDateConverter();
	}

	protected String formatTimestamp(String format, long timestamp) {
		if ("dateOnly".equals(format)) {
			return dateFormatter.formatDateTimestamp(timestamp);
		} else {
			return dateFormatter.formatTimestamp(timestamp);
		}
	}
}