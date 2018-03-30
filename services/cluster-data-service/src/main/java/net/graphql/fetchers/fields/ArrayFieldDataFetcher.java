package net.graphql.fetchers.fields;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class ArrayFieldDataFetcher extends ModelDataFetcher<List<?>> {
	private final String fieldName;

	public ArrayFieldDataFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public List<?> _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return (List<?>) entity.get(fieldName);
	}
}
