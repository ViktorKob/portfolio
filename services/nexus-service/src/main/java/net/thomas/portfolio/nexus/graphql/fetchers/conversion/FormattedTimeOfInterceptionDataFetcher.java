package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class FormattedTimeOfInterceptionDataFetcher extends FormattedTimestampDataFetcher {

	public FormattedTimeOfInterceptionDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		long timestamp;
		final Object entity = environment.getSource();
		if (entity instanceof Document) {
			timestamp = ((Document) entity).getTimeOfInterception();
		} else if (entity instanceof DocumentInfo) {
			timestamp = ((DocumentInfo) entity).getTimeOfInterception();
		} else {
			throw new RuntimeException("Unable to convert data type of type " + entity.getClass()
				.getSimpleName());
		}
		return formatTimestamp(environment, timestamp);
	}
}