package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorKnowledgeFetcher extends ModelDataFetcher<PreviousKnowledge> {

	public SelectorKnowledgeFetcher(Adaptors adaptors) {
		super(adaptors/* , 500 */);
	}

	@Override
	public PreviousKnowledge _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();
		return adaptors.getPreviousKnowledgeFor(selector.getId());
	}
}