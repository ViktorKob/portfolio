package net.thomas.portfolio.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.RecognitionLevel;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

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