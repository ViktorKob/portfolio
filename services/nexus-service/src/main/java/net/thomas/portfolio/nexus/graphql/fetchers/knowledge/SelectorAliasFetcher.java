package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;

public class SelectorAliasFetcher extends ModelDataFetcher<String> {

	public SelectorAliasFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public String _get(DataFetchingEnvironment environment) {
		final PriorKnowledge knowledge = (PriorKnowledge) environment.getSource();
		return knowledge.alias;
	}
}