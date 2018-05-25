package net.thomas.portfolio.graphql.fetchers.statistics;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;

public class SelectorStatisticsForPeriodFetcher extends ModelDataFetcher<Long> {

	private final StatisticsPeriod period;

	public SelectorStatisticsForPeriodFetcher(StatisticsPeriod period, Adaptors adaptors) {
		super(adaptors);
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