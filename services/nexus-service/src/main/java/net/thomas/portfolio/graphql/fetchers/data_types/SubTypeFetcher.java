package net.thomas.portfolio.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class SubTypeFetcher extends ModelDataFetcher<DataType> {

	private final String fieldName;

	public SubTypeFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors/* , 10 */);
		this.fieldName = fieldName;
	}

	@Override
	public DataType _get(DataFetchingEnvironment environment) {
		final DataType parent = extractOrFetchDataType(environment);
		return (DataType) parent.get(fieldName);
	}
}