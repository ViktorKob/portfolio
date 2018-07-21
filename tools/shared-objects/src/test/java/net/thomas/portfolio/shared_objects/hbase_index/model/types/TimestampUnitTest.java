package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.ZoneId;

import org.junit.Test;

public class TimestampUnitTest {
	@Test
	public void shouldBeEqual() {
		assertTrue(SOME_TIMESTAMP.equals(SOME_TIMESTAMP));
	}

	@Test
	public void shouldNotBeEqualWithDifferentTimestamp() {
		final Timestamp differentLocation = new Timestamp(SOME_TIMESTAMP.getTimestamp(), SOME_OTHER_TIME_ZONE);
		assertFalse(SOME_TIMESTAMP.equals(differentLocation));
	}

	@Test
	public void shouldNotBeEqualWithDifferentZone() {
		final Timestamp differentLocation = new Timestamp(SOME_OTHER_TIME, SOME_TIMESTAMP.getZone());
		assertFalse(SOME_TIMESTAMP.equals(differentLocation));
	}

	@Test
	public void shouldNotBeEqualWithDifferentObject() {
		assertFalse(SOME_TIMESTAMP.equals(ANOTHER_OBJECT));
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
	public void shouldSerializeAndDeserializeCorrectly() {
		final Timestamp deserializedInstance = serializeDeserialize(SOME_TIMESTAMP, Timestamp.class);
		assertEquals(SOME_TIMESTAMP, deserializedInstance);
	}

	@Test
	public void shouldContainTimestampInStringRepresentation() {
		final Long expectedContents = SOME_TIMESTAMP.getTimestamp();
		final String actualString = SOME_TIMESTAMP.toString();
		assertTrue(actualString.contains(expectedContents.toString()));
	}

	@Test
	public void shouldContainTimeZoneInStringRepresentation() {
		final ZoneId expectedContents = SOME_TIMESTAMP.getZone();
		final String actualString = SOME_TIMESTAMP.toString();
		assertTrue(actualString.contains(expectedContents.toString()));
	}

	private static final long SOME_TIME = 1l;
	private static final ZoneId SOME_TIME_ZONE = ZoneId.of("+0");
	private static final Timestamp SOME_TIMESTAMP = new Timestamp(SOME_TIME, SOME_TIME_ZONE);
	private static final long SOME_OTHER_TIME = 2l;
	private static final ZoneId SOME_OTHER_TIME_ZONE = ZoneId.of("+1");
	private static final Timestamp SOME_OTHER_TIMESTAMP = new Timestamp(SOME_OTHER_TIME, SOME_OTHER_TIME_ZONE);
	private static final Object ANOTHER_OBJECT = "object";
}
