package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class UsageActivityDocumentMutationsFetcher extends ModelDataFetcher<DataTypeId> {

	private final String documentType;

	public UsageActivityDocumentMutationsFetcher(String documentType, Adaptors adaptors) {
		super(adaptors/* , 50 */);
		this.documentType = documentType;
	}

	@Override
	public DataTypeId _get(DataFetchingEnvironment environment) {
		final String uid = (String) environment.getArgument("uid");
		return new DataTypeId(documentType, uid);
	}
}