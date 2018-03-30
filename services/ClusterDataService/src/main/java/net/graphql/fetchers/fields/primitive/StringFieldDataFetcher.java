package net.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class StringFieldDataFetcher extends ModelDataFetcher<String> {
	private final String fieldName;

	public StringFieldDataFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return entity.get(fieldName).toString();
	}
}