package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;

public class SelectorIsKnownFetcher extends ModelDataFetcher<ConfidenceLevel> {

	public SelectorIsKnownFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public ConfidenceLevel get(DataFetchingEnvironment environment) {
		final PriorKnowledge knowledge = (PriorKnowledge) environment.getSource();
		return knowledge.recognition;
	}
}