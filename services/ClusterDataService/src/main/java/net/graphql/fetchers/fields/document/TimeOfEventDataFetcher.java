package net.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.types.Document;

public class TimeOfEventDataFetcher extends ModelDataFetcher<Long> {

	public TimeOfEventDataFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Long _get(DataFetchingEnvironment environment) {
		final Document document = environment.getSource();
		return document.getTimeOfEvent();
	}
}