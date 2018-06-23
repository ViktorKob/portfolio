package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class RawDataFetcher extends ModelDataFetcher<Object> {

	public RawDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Object get(DataFetchingEnvironment environment) {
		final DataType entity = getEntity(environment);
		return entity.getInRawForm();
	}
}