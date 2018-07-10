package net.thomas.portfolio.analytics.system;

import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.POSSIBLY;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.UNLIKELY;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class AnalyticsControlUnitTest {
	private static final String SELECTOR_TYPE = "SelectorType";
	private static final char UNKNOWN_UID = '0';
	private static final char PARTIALLY_KNOWN_UID = 'A';
	private static final char KNOWN_UID = 'F';
	private static final char UNRESTRICTED_UID = '0';
	private static final char PARTIALLY_RESTRICTED_UID = 'A';
	private static final char RESTRICTED_UID = 'F';
	private static final String UID_PADDING = "0000";

	private AnalyticsControl analytics;

	@Before
	public void setupForTests() {
		analytics = new AnalyticsControl();
	}

	@Test
	public void shouldBeUnknown() {
		final DataTypeId id = buildUid(UNKNOWN_UID, UNRESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(UNLIKELY, knowledge.isKnown);
	}

	@Test
	public void shouldBePartiallyKnown() {
		final DataTypeId id = buildUid(PARTIALLY_KNOWN_UID, UNRESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(POSSIBLY, knowledge.isKnown);
	}

	@Test
	public void shouldBeKnown() {
		final DataTypeId id = buildUid(KNOWN_UID, UNRESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(CERTAIN, knowledge.isKnown);
	}

	@Test
	public void shouldBeUnrestricted() {
		final DataTypeId id = buildUid(UNKNOWN_UID, UNRESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(UNLIKELY, knowledge.isRestricted);
	}

	@Test
	public void shouldBePartiallyRestricted() {
		final DataTypeId id = buildUid(UNKNOWN_UID, PARTIALLY_RESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(POSSIBLY, knowledge.isRestricted);
	}

	@Test
	public void shouldBeRestricted() {
		final DataTypeId id = buildUid(UNKNOWN_UID, RESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(CERTAIN, knowledge.isRestricted);
	}

	@Test
	public void shouldNotHaveAliasWhenUnknown() {
		final DataTypeId id = buildUid(UNKNOWN_UID, UNRESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(null, knowledge.alias);
	}

	@Test
	public void shouldNotHaveAliasWhenPartiallyKnown() {
		final DataTypeId id = buildUid(PARTIALLY_KNOWN_UID, UNRESTRICTED_UID);
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(null, knowledge.alias);
	}

	@Test
	public void shouldHaveAliasWhenKnown() {
		final DataTypeId id = buildUid(KNOWN_UID, UNRESTRICTED_UID);
		final String fakeAlias = "Target " + id.uid;
		final AnalyticalKnowledge knowledge = analytics.getPriorKnowledge(id);
		assertEquals(fakeAlias, knowledge.alias);
	}

	private DataTypeId buildUid(char known, char restricted) {
		return new DataTypeId(SELECTOR_TYPE, "" + known + restricted + UID_PADDING);
	}
}
