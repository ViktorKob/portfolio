package net.thomas.portfolio.nexus.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;

public class TimestampFieldDataFetcher extends ModelDataFetcher<String> {
	private final String fieldName;
	private final DateConverter dateFormatter;

	public TimestampFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final DataType entity = extractOrFetchDataType(environment);
		final Long timestamp = (Long) entity.get(fieldName);
		return dateFormatter.formatTimestamp(timestamp);
	}
}