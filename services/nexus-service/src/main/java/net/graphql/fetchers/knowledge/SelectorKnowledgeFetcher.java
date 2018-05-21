package net.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.PreviousKnowledge;
import net.model.types.Selector;

public class SelectorKnowledgeFetcher extends ModelDataFetcher<PreviousKnowledge> {

	public SelectorKnowledgeFetcher(ModelAdaptor adaptor) {
		super(adaptor, 500);
	}

	@Override
	public PreviousKnowledge _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();
		return adaptor.getPreviousKnowledgeFor(selector);
	}
}