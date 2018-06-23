package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;

public class SelectorKnowledgeFetcher extends ModelDataFetcher<PriorKnowledge> {

	public SelectorKnowledgeFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public PriorKnowledge get(DataFetchingEnvironment environment) {
		return adaptors.getPriorKnowledge(getEntity(environment).getId());
	}
}