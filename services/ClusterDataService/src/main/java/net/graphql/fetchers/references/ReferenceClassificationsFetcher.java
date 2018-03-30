package net.graphql.fetchers.references;

import java.util.Set;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.Classification;
import net.model.meta_data.Reference;

public class ReferenceClassificationsFetcher extends ModelDataFetcher<Set<Classification>> {

	public ReferenceClassificationsFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Set<Classification> _get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.classifications;
	}
}