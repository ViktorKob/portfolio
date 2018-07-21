package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static java.util.Collections.emptyMap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Statistics {
	private Map<StatisticsPeriod, Long> statistics;

	public Statistics() {
		statistics = emptyMap();
	}

	public Statistics(Map<StatisticsPeriod, Long> statistics) {
		this.statistics = statistics;
	}

	public Map<StatisticsPeriod, Long> getStatistics() {
		return statistics;
	}

	public void setStatistics(Map<StatisticsPeriod, Long> statistics) {
		this.statistics = statistics;
	}

	public Long get(StatisticsPeriod period) {
		if (statistics.containsKey(period)) {
			return statistics.get(period);
		} else {
			return 0l;
		}
	}

	public int size() {
		return statistics.size();
	}

	@Override
	public int hashCode() {
		return statistics.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return statistics.equals(obj);
	}

	@Override
	public String toString() {
		return statistics.toString();
	}
}