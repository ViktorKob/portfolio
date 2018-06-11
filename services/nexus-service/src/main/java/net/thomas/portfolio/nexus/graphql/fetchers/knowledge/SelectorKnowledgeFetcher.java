package net.thomas.portfolio.nexus.graphql.fetchers.knowledge;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.analytics.PriorKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SelectorKnowledgeFetcher extends ModelDataFetcher<PriorKnowledge> {

	public SelectorKnowledgeFetcher(Adaptors adaptors) {
		super(adaptors/* , 500 */);
	}

	@Override
	public PriorKnowledge _get(DataFetchingEnvironment environment) {
		if (environment.getSource() == null) {
			return null;
		}
		DataTypeId selectorId = null;
		if (environment.getSource() instanceof Selector) {
			selectorId = ((Selector) environment.getSource()).getId();
		} else if (environment.getSource() instanceof DataTypeId) {
			selectorId = environment.getSource();
		}
		return adaptors.getPriorKnowledge(selectorId);
	}
}