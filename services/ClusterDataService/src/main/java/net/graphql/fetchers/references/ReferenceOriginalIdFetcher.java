package net.graphql.fetchers.references;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.Reference;

public class ReferenceOriginalIdFetcher extends ModelDataFetcher<String> {

	public ReferenceOriginalIdFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final Reference reference = (Reference) environment.getSource();
		return reference.originalId;
	}
}