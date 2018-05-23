package net.thomas.portfolio.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class RawDataFetcher extends ModelDataFetcher<Object> {

	public RawDataFetcher(Adaptors adaptors) {
		super(adaptors/* , 50 */);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final DataType entity = (DataType) environment.getSource();
		return entity.getInRawForm();
	}
}