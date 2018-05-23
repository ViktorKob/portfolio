package net.thomas.portfolio.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class TimeOfEventDataFetcher extends ModelDataFetcher<Long> {

	public TimeOfEventDataFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Long _get(DataFetchingEnvironment environment) {
		final Document document = environment.getSource();
		return document.getTimeOfEvent();
	}
}