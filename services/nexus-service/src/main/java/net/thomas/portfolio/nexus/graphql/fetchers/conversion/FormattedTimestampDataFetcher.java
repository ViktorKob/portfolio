package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import static net.thomas.portfolio.nexus.graphql.arguments.GraphQlArgument.DATE_FORMAT_DETAIL_LEVEL;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;

public abstract class FormattedTimestampDataFetcher extends ModelDataFetcher<String> {

	private final DateConverter dateFormatter;

	public FormattedTimestampDataFetcher(Adaptors adaptor) {
		super(adaptor);
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	protected String formatTimestampAsIec8601(DataFetchingEnvironment environment, Timestamp timestamp) {
		if (DATE_FORMAT_DETAIL_LEVEL.canBeExtractedFrom(environment)) {
			return formatTimestampAsIec8601((String) DATE_FORMAT_DETAIL_LEVEL.extractFrom(environment), timestamp.getTimestamp());
		} else {
			return formatTimestampAsIec8601((String) null, timestamp.getTimestamp());
		}
	}

	protected String formatTimestampAsIec8601(DataFetchingEnvironment environment, Long timestamp) {
		if (DATE_FORMAT_DETAIL_LEVEL.canBeExtractedFrom(environment)) {
			return formatTimestampAsIec8601((String) DATE_FORMAT_DETAIL_LEVEL.extractFrom(environment), timestamp);
		} else {
			return formatTimestampAsIec8601((String) null, timestamp);
		}
	}

	protected String formatTimestampAsIec8601(String detailLevel, Long timestamp) {
		if (timestamp == 0) {
			return "Unknown";
		} else if ("dateOnly".equals(detailLevel)) {
			return dateFormatter.formatDateTimestamp(timestamp);
		} else {
			return dateFormatter.formatTimestamp(timestamp);
		}
	}
}