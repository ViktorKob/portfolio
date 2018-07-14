package net.thomas.portfolio.hbase_index.fake.index_steps;

import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.QUARTER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.WEEK;

import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SelectorTraversalTool;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.IndexStep;
import net.thomas.portfolio.shared_objects.hbase_index.transformation.World;

public class FakeSelectorStatisticsStep implements IndexStep {
	private static final long A_DAY = 1000 * 60 * 60 * 24;
	private static final long A_WEEK = 7 * A_DAY;
	private static final long A_MONTH = 30 * A_DAY;
	private final SelectorTraversalTool traversalTool;
	private final long now;
	private final long yesterday;
	private final long oneWeekAgo;
	private final long threeMonthsAgo;

	public FakeSelectorStatisticsStep() {
		traversalTool = new SelectorTraversalTool();
		now = new GregorianCalendar(2017, 10, 17).getTimeInMillis();
		yesterday = now - A_DAY;
		oneWeekAgo = now - A_WEEK;
		threeMonthsAgo = now - 3 * A_MONTH;
	}

	@Override
	public void executeAndUpdateIndex(HbaseIndexSchema schema, World world, HbaseIndex partiallyConstructedIndex) {
		final FakeHbaseIndex index = (FakeHbaseIndex) partiallyConstructedIndex;
		index.setSelectorStatistics(generateSelectorStatistics(schema, world, partiallyConstructedIndex));
	}

	private Map<String, Map<StatisticsPeriod, Long>> generateSelectorStatistics(HbaseIndexSchema schema, World world, HbaseIndex index) {
		final Map<String, Map<StatisticsPeriod, Long>> allSelectorTotalCounts = new HashMap<>();
		for (final DataType entity : world.getEvents()) {
			final Document document = (Document) entity;
			final Map<String, DataType> selectors = traversalTool.grabSelectorsFromSubtree(entity, schema);
			for (final DataType selector : selectors.values()) {
				ensurePresenceOfCounters(allSelectorTotalCounts, selector.getId().uid);
				updateCounts(allSelectorTotalCounts, document, selector.getId().uid);
			}
		}
		return allSelectorTotalCounts;
	}

	private void ensurePresenceOfCounters(final Map<String, Map<StatisticsPeriod, Long>> allSelectorTotalCounts, final String uid) {
		if (!allSelectorTotalCounts.containsKey(uid)) {
			allSelectorTotalCounts.put(uid, blankSelectorStatistics());
		}
	}

	private void updateCounts(final Map<String, Map<StatisticsPeriod, Long>> allSelectorTotalCounts, final Document document, final String uid) {
		final Map<StatisticsPeriod, Long> statistics = allSelectorTotalCounts.get(uid);
		statistics.put(INFINITY, statistics.get(INFINITY) + 1);
		if (document.getTimeOfEvent() > threeMonthsAgo) {
			statistics.put(QUARTER, statistics.get(QUARTER) + 1);
			if (document.getTimeOfEvent() > oneWeekAgo) {
				statistics.put(WEEK, statistics.get(WEEK) + 1);
				if (document.getTimeOfEvent() > yesterday) {
					statistics.put(DAY, statistics.get(DAY) + 1);
				}
			}
		}
	}

	private Map<StatisticsPeriod, Long> blankSelectorStatistics() {
		final Map<StatisticsPeriod, Long> statistics = new EnumMap<>(StatisticsPeriod.class);
		for (final StatisticsPeriod period : StatisticsPeriod.values()) {
			statistics.put(period, 0l);
		}
		return statistics;
	}
}