package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.analytics.RecognitionLevel;

public class SelectorIsDanishFetcher extends ModelDataFetcher<RecognitionLevel> {

	public SelectorIsDanishFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public RecognitionLevel _get(DataFetchingEnvironment environment) {
		final PriorKnowledge knowledge = (PriorKnowledge) environment.getSource();
		return knowledge.isDanish;
	}
}