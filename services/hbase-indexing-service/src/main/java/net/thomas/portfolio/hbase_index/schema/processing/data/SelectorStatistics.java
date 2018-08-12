package net.thomas.portfolio.hbase_index.schema.processing.data;

import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.QUARTER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.WEEK;

import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class SelectorStatistics {
	private static final long A_DAY = 1000 * 60 * 60 * 24;
	private static final long A_WEEK = 7 * A_DAY;
	private static final long A_MONTH = 30 * A_DAY;
	private final long now;
	private final long yesterday;
	private final long oneWeekAgo;
	private final long threeMonthsAgo;
	private final Map<String, Map<StatisticsPeriod, Long>> statistics;

	public SelectorStatistics() {
		statistics = new HashMap<>();
		now = new GregorianCalendar(2017, 10, 17).getTimeInMillis();
		yesterday = now - A_DAY;
		oneWeekAgo = now - A_WEEK;
		threeMonthsAgo = now - 3 * A_MONTH;
	}

	public void updateCounts(SelectorEntity selector, Event event) {
		final Map<StatisticsPeriod, Long> period2Counts = createOrGetCountsBySelectorUid(selector);
		updateCounts(period2Counts, selector, event);
	}

	private Map<StatisticsPeriod, Long> createOrGetCountsBySelectorUid(SelectorEntity selector) {
		if (!statistics.containsKey(selector.uid)) {
			statistics.put(selector.uid, createBlankSelectorStatistics());
		}
		return statistics.get(selector.uid);
	}

	private Map<StatisticsPeriod, Long> createBlankSelectorStatistics() {
		final Map<StatisticsPeriod, Long> statistics = new EnumMap<>(StatisticsPeriod.class);
		for (final StatisticsPeriod period : StatisticsPeriod.values()) {
			statistics.put(period, 0l);
		}
		return statistics;
	}

	private void updateCounts(final Map<StatisticsPeriod, Long> statistics, SelectorEntity selector, Event event) {
		final Timestamp timeOfEvent = event.timeOfEvent;
		statistics.put(INFINITY, statistics.get(INFINITY) + 1);
		if (timeOfEvent.isAfter(threeMonthsAgo)) {
			statistics.put(QUARTER, statistics.get(QUARTER) + 1);
			if (timeOfEvent.isAfter(oneWeekAgo)) {
				statistics.put(WEEK, statistics.get(WEEK) + 1);
				if (timeOfEvent.isAfter(yesterday)) {
					statistics.put(DAY, statistics.get(DAY) + 1);
				}
			}
		}
	}

	public Map<StatisticsPeriod, Long> get(String uid) {
		if (statistics.containsKey(uid)) {
			return statistics.get(uid);
		} else {
			return createBlankSelectorStatistics();
		}
	}
}