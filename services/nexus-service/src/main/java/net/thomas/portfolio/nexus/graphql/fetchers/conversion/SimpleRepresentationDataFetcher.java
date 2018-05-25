package net.thomas.portfolio.nexus.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationDataFetcher extends ModelDataFetcher<Object> {

	public SimpleRepresentationDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();
		return adaptors.renderAsSimpleRepresentation(selector.getId());
	}
}