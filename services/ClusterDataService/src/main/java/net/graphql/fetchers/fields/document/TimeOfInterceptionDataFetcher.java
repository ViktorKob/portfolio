package net.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.types.Document;

public class TimeOfInterceptionDataFetcher extends ModelDataFetcher<Long> {

	public TimeOfInterceptionDataFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Long _get(DataFetchingEnvironment environment) {
		final Document document = environment.getSource();
		return document.getTimeOfInterception();
	}
}