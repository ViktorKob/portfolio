package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class ArrayFieldDataFetcher extends ModelDataFetcher<List<?>> {
	private final String fieldName;

	public ArrayFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public List<?> _get(DataFetchingEnvironment environment) {
		final DataType entity = extractOrFetchDataType(environment);
		return (List<?>) entity.get(fieldName);
	}
}
