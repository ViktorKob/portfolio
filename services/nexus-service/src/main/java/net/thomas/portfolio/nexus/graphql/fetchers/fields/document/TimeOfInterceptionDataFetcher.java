package net.thomas.portfolio.nexus.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class TimeOfInterceptionDataFetcher extends ModelDataFetcher<Long> {

	public TimeOfInterceptionDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Long _get(DataFetchingEnvironment environment) {
		final Object entity = environment.getSource();
		if (environment.getSource() instanceof Document) {
			return ((Document) entity).getTimeOfInterception();
		} else if (entity instanceof DocumentInfo) {
			return ((DocumentInfo) entity).getTimeOfInterception();
		} else {
			throw new RuntimeException("Unable to convert data type of type " + entity.getClass()
					.getSimpleName());
		}
	}
}