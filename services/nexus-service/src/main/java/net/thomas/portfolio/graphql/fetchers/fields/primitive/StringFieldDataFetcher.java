package net.thomas.portfolio.graphql.fetchers.fields.primitive;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class StringFieldDataFetcher extends ModelDataFetcher<String> {
	private final String fieldName;

	public StringFieldDataFetcher(String fieldName, HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return entity.get(fieldName).toString();
	}
}