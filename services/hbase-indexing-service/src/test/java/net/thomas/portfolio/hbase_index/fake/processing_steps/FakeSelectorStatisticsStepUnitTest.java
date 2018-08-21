package net.thomas.portfolio.hbase_index.fake.processing_steps;

import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.fake.FakeHbaseIndex;
import net.thomas.portfolio.hbase_index.fake.FakeWorldStorage;
import net.thomas.portfolio.hbase_index.fake.generators.FakeWorldGenerator;
import net.thomas.portfolio.hbase_index.fake.world.storage.EventWriter;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.processing.utils.SelectorExtractor;
import net.thomas.portfolio.hbase_index.schema.selectors.SelectorEntity;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class FakeSelectorStatisticsStepUnitTest {
	private FakeWorldStorage events;
	private FakeHbaseIndex index;
	private SelectorExtractor selectorExtractor;
	private FakeSelectorStatisticsStep selectorStatisticsStep;

	@Before
	public void setUpForTest() {
		selectorExtractor = new SelectorExtractor();
		events = new FakeWorldStorage();
		new FakeWorldGenerator(1234L, 5, 10, 10).generateAndWrite((EventWriter) events);
		index = new FakeHbaseIndex();
		index.setEventReader(events);
		selectorStatisticsStep = new FakeSelectorStatisticsStep();
		selectorStatisticsStep.executeAndUpdateIndex(events, index);
	}

	@Test
	public void shouldContainStatisticsForAllSelectors() {
		for (final Event event : events) {
			final Set<SelectorEntity> selectors = selectorExtractor.extract(event);
			for (final SelectorEntity selector : selectors) {
				assertTrue("Could not find statistics for event using " + selector + " with " + event,
						0 < getStatisticsCount(selector));
			}
		}
	}

	private long getStatisticsCount(final SelectorEntity selector) {
		final String selectorType = simpleName(selector);
		final Statistics statistics = index.getStatistics(new DataTypeId(selectorType, selector.uid));
		return statistics.getStatistics().get(INFINITY);
	}

	private String simpleName(final SelectorEntity entity) {
		return entity.getClass().getSimpleName();
	}
}