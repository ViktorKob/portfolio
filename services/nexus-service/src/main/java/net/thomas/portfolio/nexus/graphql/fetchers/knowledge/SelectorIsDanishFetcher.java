package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.analytics.RecognitionLevel;

public class SelectorIsDanishFetcher extends ModelDataFetcher<RecognitionLevel> {

	public SelectorIsDanishFetcher(Adaptors adaptors) {
		super(adaptors);
	}

	@Override
	public RecognitionLevel _get(DataFetchingEnvironment environment) {
		final PreviousKnowledge knowledge = (PreviousKnowledge) environment.getSource();
		return knowledge.isDanish;
	}
}