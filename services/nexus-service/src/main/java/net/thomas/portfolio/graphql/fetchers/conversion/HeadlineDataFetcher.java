package net.thomas.portfolio.graphql.fetchers.conversion;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class HeadlineDataFetcher extends ModelDataFetcher<String> {

	public HeadlineDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return adaptors.renderAsText(entity);
	}
}