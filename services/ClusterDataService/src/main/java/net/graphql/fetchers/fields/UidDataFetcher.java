package net.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class UidDataFetcher extends ModelDataFetcher<Object> {

	public UidDataFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return entity.getUid();
	}
}