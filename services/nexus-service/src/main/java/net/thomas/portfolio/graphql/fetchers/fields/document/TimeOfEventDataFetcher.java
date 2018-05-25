package net.thomas.portfolio.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class TimeOfEventDataFetcher extends ModelDataFetcher<Long> {

	public TimeOfEventDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Long _get(DataFetchingEnvironment environment) {
		final Object entity = environment.getSource();
		if (environment.getSource() instanceof Document) {
			return ((Document) entity).getTimeOfEvent();
		} else if (entity instanceof DocumentInfo) {
			return ((DocumentInfo) entity).getTimeOfEvent();
		} else {
			throw new RuntimeException("Unable to convert data type of type " + entity.getClass()
				.getSimpleName());
		}
	}
}