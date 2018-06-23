package net.thomas.portfolio.nexus.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DataTypeIdProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class DataTypeFetcher extends ModelDataFetcher<DataTypeIdProxy> {

	private final String type;

	public DataTypeFetcher(String type, Adaptors adaptors) {
		super(adaptors);
		this.type = type;
	}

	@Override
	public DataTypeIdProxy get(DataFetchingEnvironment environment) {
		final Object uid = environment.getArgument("uid");
		if (uid != null) {
			final DataTypeId id = new DataTypeId(type, uid.toString());
			return new DataTypeIdProxy(id, adaptors);
		} else {
			return null;
		}
	}
}