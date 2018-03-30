package net.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.DataType;

public class RawDataFetcher extends ModelDataFetcher<Object> {

	public RawDataFetcher(ModelAdaptor adaptor) {
		super(adaptor, 50);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return entity.getInRawForm();
	}
}