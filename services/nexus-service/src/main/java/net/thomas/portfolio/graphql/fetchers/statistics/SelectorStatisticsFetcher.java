package net.thomas.portfolio.graphql.fetchers.statistics;

import java.util.Collections;
import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SelectorStatisticsFetcher extends ModelDataFetcher<Map<StatisticsPeriod, Long>> {

	public SelectorStatisticsFetcher(HbaseModelAdaptor adaptor) {
		super(adaptor, 50);
	}

	@Override
	public Map<StatisticsPeriod, Long> _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();

		// if ((selector.getJustification() == null || selector.getJustification().isEmpty()) &&
		// adaptor.getPreviousKnowledgeFor(selector).isDanish == KNOWN) {
		// return Collections.emptyMap();
		// }
		final Map<StatisticsPeriod, Long> statistics = adaptor.getStatistics(selector);
		if (statistics != null) {
			return statistics;
		} else {
			return Collections.emptyMap();
		}
	}
}