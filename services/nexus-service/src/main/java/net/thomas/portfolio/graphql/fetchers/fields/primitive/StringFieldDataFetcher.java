package net.thomas.portfolio.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class StringFieldDataFetcher extends ModelDataFetcher<String> {
	private final String fieldName;

	public StringFieldDataFetcher(String fieldName, Adaptors adaptors) {
		super(adaptors);
		this.fieldName = fieldName;
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final DataType entity = extractOrFetchDataType(environment);
		return entity.get(fieldName)
			.toString();
	}
}