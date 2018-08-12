package net.thomas.portfolio.shared_objects.hbase_index.model.meta_data;

import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.DAY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.EnumMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class StatisticsUnitTest {
	private Statistics statistics;
	private Map<StatisticsPeriod, Long> actualContainer;

	@Before
	public void setUpForTest() {
		actualContainer = new EnumMap<>(StatisticsPeriod.class);
		statistics = new Statistics(actualContainer);
		actualContainer.put(INFINITY, SOME_STATISTIC);
	}

	@Test
	public void shouldBeEmptyInitially() {
		assertFalse(new Statistics().hasData());
	}

	@Test
	public void shouldOverrideOwnContainerOnDemand() {
		final Statistics statistics = new Statistics();
		statistics.setStatistics(actualContainer);
		assertSame(actualContainer, statistics.getStatistics());
	}

	@Test
	public void shouldContainElementsWhenContainerDoes() {
		statistics.setStatistics(actualContainer);
		assertTrue(statistics.hasData());
	}

	@Test
	public void shouldGetValueWhenPresent() {
		statistics.setStatistics(actualContainer);
		assertEquals(SOME_STATISTIC, statistics.get(INFINITY));
	}

	@Test
	public void shouldGetZeroWhenNotPresent() {
		assertEquals((Long) 0L, statistics.get(DAY));
	}

	@Test
	public void shouldForwardHashCodeCallToInnerContainer() {
		assertEquals(actualContainer.hashCode(), statistics.hashCode());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void shouldForwardEqualsCallToInnerContainer() {
		assertTrue(statistics.equals(actualContainer));
	}

	@Test
	public void shouldForwardToStringCallToInnerContainer() {
		assertEquals(actualContainer.toString(), statistics.toString());
	}

	private static final Long SOME_STATISTIC = 1L;
}
