package net.thomas.portfolio.nexus.graphql.fetchers.fields.document;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class RawDataFetcher extends ModelDataFetcher<Object> {

	public RawDataFetcher(Adaptors adaptors) {
		super(adaptors/* , 50 */);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final DataType entity = extractOrFetchDataType(environment);
		return entity.getInRawForm();
	}
}