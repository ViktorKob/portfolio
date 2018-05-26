package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorKnowledgeFetcher extends ModelDataFetcher<PriorKnowledge> {

	public SelectorKnowledgeFetcher(Adaptors adaptors) {
		super(adaptors/* , 500 */);
	}

	@Override
	public PriorKnowledge _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();
		return adaptors.getPriorKnowledge(selector.getId());
	}
}