package net.thomas.portfolio.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.Adaptors;

public class SelectorKnowledgeFetcher extends ModelDataFetcher<PreviousKnowledge> {

	public SelectorKnowledgeFetcher(Adaptors adaptors) {
		super(adaptors/* , 500 */);
	}

	@Override
	public PreviousKnowledge _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();
		return adaptors.getPreviousKnowledgeFor(selector);
	}
}