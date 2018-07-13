package net.thomas.portfolio.nexus.graphql.fetchers.statistics;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.nexus.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;

public class SelectorStatisticsForPeriodFetcher extends ModelDataFetcher<Long> {

	private final StatisticsPeriod period;

	public SelectorStatisticsForPeriodFetcher(StatisticsPeriod period, Adaptors adaptors) {
		super(adaptors);
		this.period = period;
	}

	@Override
	public Long get(DataFetchingEnvironment environment) {
		final Map<StatisticsPeriod, Long> statistics = environment.getSource();
		final Long count = statistics.get(period);
		if (count != null) {
			return count;
		} else {
			return 0l;
		}
	}
}