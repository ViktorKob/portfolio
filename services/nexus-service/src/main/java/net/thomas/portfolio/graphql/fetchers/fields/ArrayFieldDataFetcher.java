package net.thomas.portfolio.graphql.fetchers.fields;

import java.util.List;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class ArrayFieldDataFetcher extends ModelDataFetcher<List<?>> {
	private final String fieldName;

	public ArrayFieldDataFetcher(String fieldName, HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
		this.fieldName = fieldName;
	}

	@Override
	public List<?> _get(DataFetchingEnvironment environment) {
		final Datatype entity = (Datatype) environment.getSource();
		return (List<?>) entity.get(fieldName);
	}
}
