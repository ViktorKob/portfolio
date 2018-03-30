package net.graphql.fetchers.data_types;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class SubTypeArrayFetcher extends ModelDataFetcher<List<?>> {

	private final String fieldName;

	public SubTypeArrayFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 50);
		this.fieldName = fieldName;
	}

	@Override
	public List<?> _get(DataFetchingEnvironment environment) {
		final DataType parent = environment.getSource();
		return (List<?>) parent.get(fieldName);
	}
}