package net.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.PreviousKnowledge;
import net.model.meta_data.RecognitionLevel;

public class SelectorIsKnownFetcher extends ModelDataFetcher<RecognitionLevel> {

	public SelectorIsKnownFetcher(ModelAdaptor adaptor) {
		super(adaptor, 0);
	}

	@Override
	public RecognitionLevel _get(DataFetchingEnvironment environment) {
		final PreviousKnowledge knowledge = (PreviousKnowledge) environment.getSource();
		return knowledge.isDanish;
	}
}