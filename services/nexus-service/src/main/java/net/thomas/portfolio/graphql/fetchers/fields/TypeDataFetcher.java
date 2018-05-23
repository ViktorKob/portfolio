package net.thomas.portfolio.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.Datatype;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class TypeDataFetcher extends ModelDataFetcher<Object> {

	public TypeDataFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final Datatype entity = (Datatype) environment.getSource();
		return entity.getType();
	}
}