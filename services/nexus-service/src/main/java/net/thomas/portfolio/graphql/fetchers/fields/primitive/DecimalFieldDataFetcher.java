package net.thomas.portfolio.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class DecimalFieldDataFetcher extends ModelDataFetcher<Double> {
	private final String fieldName;

	public DecimalFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
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