package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class Statistics extends EnumMap<StatisticsPeriod, Long> {
	private static final long serialVersionUID = 1L;

	public Statistics() {
		super(StatisticsPeriod.class);
	}

	public Statistics(Map<String, Integer> values) {
		super(StatisticsPeriod.class);
		// TODO[Thomas]: Loss of precision; deserialize directly as long instead
		for (final Entry<String, Integer> entry : values.entrySet()) {
			put(StatisticsPeriod.valueOf(entry.getKey()), entry.getValue()
				.longValue());
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}