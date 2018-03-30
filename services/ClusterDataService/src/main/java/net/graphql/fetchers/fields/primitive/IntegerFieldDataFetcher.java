package net.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class IntegerFieldDataFetcher extends ModelDataFetcher<Long> {
	private final String fieldName;

	public IntegerFieldDataFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public Long _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		final Object value = entity.get(fieldName);
		if (value instanceof Long) {
			return (Long) value;
		} else if (value instanceof Integer) {
			return Long.valueOf((int) value);
		} else {
			return Long.valueOf(value.toString());
		}
	}
}