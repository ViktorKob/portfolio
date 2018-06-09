package net.thomas.portfolio.nexus.graphql.fetchers.usage_data;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;

public class UsageActivityDocumentsMutationsFetcher extends ModelDataFetcher<Object> {

	public UsageActivityDocumentsMutationsFetcher(Adaptors adaptors) {
		super(adaptors/* , 50 */);
	}

	@Override
	public Object _get(DataFetchingEnvironment environment) {
		return "Dummy value != null, to make GraphQL continue past this node";
	}
}