package net.graphql.fetchers.statistics;

import static net.model.meta_data.RecognitionLevel.KNOWN;

import java.util.Collections;
import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.StatisticsPeriod;
import net.model.types.Selector;

public class SelectorStatisticsFetcher extends ModelDataFetcher<Map<StatisticsPeriod, Long>> {

	public SelectorStatisticsFetcher(ModelAdaptor adaptor) {
		super(adaptor, 50);
	}

	@Override
	public Map<StatisticsPeriod, Long> _get(DataFetchingEnvironment environment) {
		final Selector selector = (Selector) environment.getSource();

		if ((selector.getJustification() == null || selector.getJustification().isEmpty()) &&
				adaptor.getPreviousKnowledgeFor(selector).isDanish == KNOWN) {
			return Collections.emptyMap();
		}
		final Map<StatisticsPeriod, Long> statistics = adaptor.getStatisticsFor(selector);
		if (statistics != null) {
			return statistics;
		} else {
			return Collections.emptyMap();
		}
	}
}