package net.thomas.portfolio.graphql.fetchers.fields;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class ArrayFieldDataFetcher extends ModelDataFetcher<List<?>> {
	private final String fieldName;

	public ArrayFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public List<?> _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return (List<?>) entity.get(fieldName);
	}
}
