package net.thomas.portfolio.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class SimpleRepresentationDataFetcher extends ModelDataFetcher<Object> {
	// private final Renderer<String> renderer;

	public SimpleRepresentationDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return adaptors.renderAsSimpleRepresentation(entity);
	}
}