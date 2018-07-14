package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class IntegerFieldDataFetcher extends ModelDataFetcher<Long> {
	private final String fieldName;

	public IntegerFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public Long get(DataFetchingEnvironment environment) {
		final DataType entity = getEntity(environment);
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