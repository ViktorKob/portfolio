package net.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class DecimalFieldDataFetcher extends ModelDataFetcher<Double> {
	private final String fieldName;

	public DecimalFieldDataFetcher(String fieldName, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public Double _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		final Object value = entity.get(fieldName);
		if (value instanceof Double) {
			return (Double) value;
		} else if (value instanceof Float) {
			return Double.valueOf((float) value);
		} else {
			return Double.valueOf(value.toString());
		}
	}
}