package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;

public class TypeDataFetcher extends ModelDataFetcher<Object> {

	public TypeDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		final Object entity = environment.getSource();
		if (entity instanceof DataType) {
			return ((DataType) entity).getId().type;
		} else if (entity instanceof DataTypeId) {
			return ((DataTypeId) entity).type;
		} else if (entity instanceof DocumentInfo) {
			return ((DocumentInfo) entity).getId().type;
		}
		throw new RuntimeException("Unable to convert data type of type " + entity.getClass()
			.getSimpleName());
	}
}