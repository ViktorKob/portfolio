package net.graphql.fetchers.data_types;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;

public class EntityFetcher<ENTITY_TYPE> extends ModelDataFetcher<ENTITY_TYPE> {

	protected final String type;

	public EntityFetcher(String type, ModelAdaptor adaptor) {
		super(adaptor, 50);
		this.type = type;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ENTITY_TYPE _get(DataFetchingEnvironment environment) {
		final Object uid = environment.getArgument("uid");
		if (uid != null) {
			return (ENTITY_TYPE) adaptor.getDataTypeByUid(type, uid.toString());
		}
		return null;
	}
}