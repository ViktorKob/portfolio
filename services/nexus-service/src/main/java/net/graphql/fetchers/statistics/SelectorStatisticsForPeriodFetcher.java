package net.graphql.fetchers.statistics;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.graphql.fetchers.ModelAdaptor;
import net.graphql.fetchers.ModelDataFetcher;
import net.model.meta_data.StatisticsPeriod;

public class SelectorStatisticsForPeriodFetcher extends ModelDataFetcher<Long> {

	private final StatisticsPeriod period;

	public SelectorStatisticsForPeriodFetcher(StatisticsPeriod period, ModelAdaptor adaptor) {
		super(adaptor, 0);
		this.period = period;
	}

	@Override
	public Long _get(DataFetchingEnvironment environment) {
		@SuppressWarnings("unchecked")
		final Map<StatisticsPeriod, Long> statistics = (Map<StatisticsPeriod, Long>) environment.getSource();
		final Long count = statistics.get(period);
		if (count != null) {
			return count;
		} else {
			return 0l;
		}
	}
}