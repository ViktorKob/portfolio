package net.thomas.portfolio.graphql.fetchers.statistics;

import java.util.Map;

import graphql.schema.DataFetchingEnvironment;
import net.thomas.portfolio.graphql.fetchers.ModelDataFetcher;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseModelAdaptor;

public class SelectorStatisticsForPeriodFetcher extends ModelDataFetcher<Long> {

	private final StatisticsPeriod period;

	public SelectorStatisticsForPeriodFetcher(StatisticsPeriod period, HbaseModelAdaptor adaptor) {
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