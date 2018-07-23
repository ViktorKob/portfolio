package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter;

public class FormattedTimestampFieldDataFetcher extends ModelDataFetcher<String> {
	private final String fieldName;
	private final DateConverter dateFormatter;

	public FormattedTimestampFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
		dateFormatter = adaptors.getIec8601DateConverter();
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final Timestamp timestamp = ((Document) getEntity(environment)).get(fieldName);
		return dateFormatter.formatTimestamp(timestamp.getTimestamp());
	}
}