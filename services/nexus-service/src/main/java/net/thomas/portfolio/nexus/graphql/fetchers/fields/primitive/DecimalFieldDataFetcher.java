package net.thomas.portfolio.nexus.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class DecimalFieldDataFetcher extends ModelDataFetcher<Double> {
	private final String fieldName;

	public DecimalFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public Double _get(DataFetchingEnvironment environment) {
		final DataType entity = extractOrFetchDataType(environment);
		if (entity != null) {
			final Object value = entity.get(fieldName);
			if (value instanceof Double) {
				return (Double) value;
			} else if (value instanceof Float) {
				return Double.valueOf((float) value);
			} else {
				return Double.valueOf(value.toString());
			}
		} else {
			return null;
		}
	}
}