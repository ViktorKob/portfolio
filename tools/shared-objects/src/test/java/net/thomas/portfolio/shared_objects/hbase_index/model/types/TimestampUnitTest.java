package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;

import org.junit.Test;

public class TimestampUnitTest {

	@Test
	public void shouldAddZoneIdUsingConstructor() {
		final Timestamp timestamp = new Timestamp(SOME_TIME, SOME_TIME_ZONE.getId());
		assertEquals(SOME_TIME_ZONE, timestamp.getOriginalTimeZone());
	}

	@Test
	public void shouldChangeTimeZone() {
		final Timestamp timestamp = new Timestamp(SOME_TIME, SOME_TIME_ZONE.getId());
		timestamp.setOriginalTimeZone(SOME_OTHER_TIME_ZONE);
		assertEquals(SOME_OTHER_TIME_ZONE, timestamp.getOriginalTimeZone());
	}

	@Test
	public void shouldBeBeforeTime() {
		assertTrue(SOME_TIMESTAMP.isBefore(SOME_LATER_TIME));
	}

	@Test
	public void shouldNotBeBeforeTime() {
		assertFalse(SOME_TIMESTAMP.isBefore(SOME_EARLIER_TIME));
	}

	@Test
	public void shouldBeBeforeTimestamp() {
		final Timestamp otherTimestamp = new Timestamp(SOME_LATER_TIME);
		assertTrue(SOME_TIMESTAMP.isBefore(otherTimestamp));
	}

	@Test
	public void shouldBeAfterTime() {
		assertTrue(SOME_TIMESTAMP.isAfter(SOME_EARLIER_TIME));
	}

	@Test
	public void shouldNotBeAfterTime() {
		assertFalse(SOME_TIMESTAMP.isAfter(SOME_LATER_TIME));
	}

	@Test
	public void shouldBeAfterTimestamp() {
		final Timestamp otherTimestamp = new Timestamp(SOME_EARLIER_TIME);
		assertTrue(SOME_TIMESTAMP.isAfter(otherTimestamp));
	}

	@Test
	public void shouldHaveValidEquals() {
		assertEquals(SOME_TIMESTAMP, SOME_TIMESTAMP);
		assertNotEquals(SOME_TIMESTAMP, null);
		assertNotEquals(SOME_TIMESTAMP, "");
		assertNotEquals(SOME_TIMESTAMP, SOME_OTHER_TIMESTAMP);
		assertEquals(SOME_TIMESTAMP, new Timestamp(SOME_TIMESTAMP.getTimestamp(), SOME_TIMESTAMP.getOriginalTimeZone()));
	}

	@Test
	public void shouldNotBeEqualWithDifferentTimestamp() {
		final Timestamp differentLocation = new Timestamp(SOME_TIMESTAMP.getTimestamp(), SOME_OTHER_TIME_ZONE);
		assertFalse(SOME_TIMESTAMP.equals(differentLocation));
	}

	@Test
	public void shouldNotBeEqualWithDifferentZone() {
		final Timestamp differentLocation = new Timestamp(SOME_OTHER_TIME, SOME_TIMESTAMP.getOriginalTimeZone());
		assertFalse(SOME_TIMESTAMP.equals(differentLocation));
	}

	@Test
	public void shouldHaveSameHashCode() {
		assertEquals(SOME_TIMESTAMP.hashCode(), SOME_TIMESTAMP.hashCode());
	}

	@Test
	public void shouldNotHaveSameHashCode() {
		assertNotEquals(SOME_TIMESTAMP.hashCode(), SOME_OTHER_TIMESTAMP.hashCode());
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(SOME_TIMESTAMP);
	}

	@Test
	public void shouldContainTimestampInStringRepresentation() {
		final Long expectedContents = SOME_TIMESTAMP.getTimestamp();
		final String actualString = SOME_TIMESTAMP.toString();
		assertTrue(actualString.contains(expectedContents.toString()));
	}

	@Test
	public void shouldContainTimeZoneInStringRepresentation() {
		final ZoneId expectedContents = SOME_TIMESTAMP.getOriginalTimeZone();
		final String actualString = SOME_TIMESTAMP.toString();
		assertTrue(actualString.contains(expectedContents.toString()));
	}

	private static final long SOME_EARLIER_TIME = 1l;
	private static final long SOME_TIME = 2l;
	private static final long SOME_LATER_TIME = 3l;
	private static final long SOME_OTHER_TIME = 4l;
	private static final ZoneId SOME_TIME_ZONE = ZoneId.of("+0");
	private static final Timestamp SOME_TIMESTAMP = new Timestamp(SOME_TIME, SOME_TIME_ZONE);
	private static final ZoneId SOME_OTHER_TIME_ZONE = ZoneId.of("+1");
	private static final Timestamp SOME_OTHER_TIMESTAMP = new Timestamp(SOME_OTHER_TIME, SOME_OTHER_TIME_ZONE);
}
