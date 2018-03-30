package net.graphql.fetchers.references;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.Reference;
import net.model.meta_data.Source;

public class ReferenceSourceFetcher extends ModelDataFetcher<Source> {

	public ReferenceSourceFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Source _get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.source;
	}
}