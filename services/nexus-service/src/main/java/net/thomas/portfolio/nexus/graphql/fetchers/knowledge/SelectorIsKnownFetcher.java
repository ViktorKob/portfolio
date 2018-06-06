package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel;

public class SelectorIsKnownFetcher extends ModelDataFetcher<ConfidenceLevel> {

	public SelectorIsKnownFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public ConfidenceLevel _get(DataFetchingEnvironment environment) {
		final PriorKnowledge knowledge = (PriorKnowledge) environment.getSource();
		return knowledge.recognition;
	}
}