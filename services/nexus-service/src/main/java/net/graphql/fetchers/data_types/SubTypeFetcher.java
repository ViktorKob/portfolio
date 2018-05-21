package net.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class SubTypeFetcher extends ModelDataFetcher<DataType> {

	private final String fieldName;

	public SubTypeFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 10);
		this.fieldName = fieldName;
	}

	@Override
	public DataType _get(DataFetchingEnvironment environment) {
		final DataType parent = environment.getSource();
		return (DataType) parent.get(fieldName);
	}
}