package net.thomas.portfolio.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.PreviousKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SelectorKnowledgeFetcher extends ModelDataFetcher<PreviousKnowledge> {

	public SelectorKnowledgeFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 500);
	}

	@Override
	public PreviousKnowledge _get(DataFetchingEnvironment environment) {
		// final Selector selector = (Selector) environment.getSource();
		// return adaptor.getPreviousKnowledgeFor(selector);
		return null;
	}
}