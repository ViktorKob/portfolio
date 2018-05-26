package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class UidDataFetcher extends ModelDataFetcher<Object> {

	public UidDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final Object entity = environment.getSource();
		if (entity instanceof DataType) {
			return ((DataType) entity).getId().uid;
		} else if (entity instanceof DocumentInfo) {
			return ((DocumentInfo) entity).getId().uid;
		}
		throw new RuntimeException("Unable to convert data type of type " + entity.getClass()
			.getSimpleName());
	}
}