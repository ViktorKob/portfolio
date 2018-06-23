package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.nexus.graphql.fetchers.data_proxies.DataTypeIdProxy;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class UsageActivityDocumentMutationsFetcher extends ModelDataFetcher<DataTypeIdProxy> {

	private final String documentType;

	public UsageActivityDocumentMutationsFetcher(String documentType, Adaptors adaptors) {
		super(adaptors);
		this.documentType = documentType;
	}

	@Override
	public DataTypeIdProxy get(DataFetchingEnvironment environment) {
		final String uid = (String) environment.getArgument("uid");
		return new DataTypeIdProxy(new DataTypeId(documentType, uid), adaptors);
	}
}