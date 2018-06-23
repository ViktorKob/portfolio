package net.thomas.portfolio.nexus.graphql.fetchers.fields;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class UidDataFetcher extends ModelDataFetcher<String> {

	public UidDataFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String get(DataFetchingEnvironment environment) {
		final DataTypeId id = getId(environment);
		return id.uid;
	}
}