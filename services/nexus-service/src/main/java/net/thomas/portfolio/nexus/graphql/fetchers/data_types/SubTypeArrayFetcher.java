package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class SubTypeArrayFetcher extends ModelDataFetcher<List<?>> {

	private final String fieldName;

	public SubTypeArrayFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors/* , 50 */);
		this.fieldName = fieldName;
	}

	@Override
	public List<?> _get(DataFetchingEnvironment environment) {
		final DataType parent = environment.getSource();
		return (List<?>) parent.get(fieldName);
	}
}