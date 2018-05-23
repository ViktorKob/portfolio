package net.thomas.portfolio.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class EntityFetcher<ENTITY_TYPE> extends ModelDataFetcher<ENTITY_TYPE> {

	protected final String type;

	public EntityFetcher(String type, Adaptors adaptors) {
		super(adaptors/* , 50 */);
		this.type = type;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ENTITY_TYPE _get(DataFetchingEnvironment environment) {
		final Object uid = environment.getArgument("uid");
		if (uid != null) {
			return (ENTITY_TYPE) adaptors.getDataTypeByUid(type, uid.toString());
		}
		return null;
	}
}