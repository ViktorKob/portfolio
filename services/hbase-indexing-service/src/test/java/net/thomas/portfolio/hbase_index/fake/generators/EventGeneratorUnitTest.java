package net.thomas.portfolio.hbase_index.fake.generators;

import static java.lang.System.nanoTime;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class EventGeneratorUnitTest {
	private static final long SOME_SEED = nanoTime();
	private static final long START_DATE = asLong(new GregorianCalendar(2017, 4, 17).getTime());
	private static final long END_DATE = asLong(new GregorianCalendar(2017, 10, 17).getTime());
	private static final double GUARANTEED_TO_EXIST = 1.0;
	private static final double GUARANTEED_NOT_TO_EXIST = 0.0;

	private TestEventGenerator generator;

	@Before
	public void setUpForTest() {
		generator = new TestEventGenerator(SOME_SEED);
	}

	@Test
	public void shouldGenerateEventOnDemand() {
		final TestEvent event = generator.next();
		assertNotNull(event);
	}

	@Test
	public void shouldGenerateTimeOfEventBetweenBounds() {
		for (int i = 0; i < 100; i++) {
			final long timeOfEvent = asLong(generator.generateTimeOfEvent());
			assertTrue(START_DATE <= timeOfEvent);
			assertTrue(END_DATE >= timeOfEvent);
		}
	}

	@Test
	public void shouldGenerateTimeOfInterceptionLaterThanTimeOfEvent() {
		for (int i = 0; i < 100; i++) {
			final long timeOfInterception = asLong(generator.generateTimeOfInterception(new Timestamp(START_DATE)));
			assertTrue(timeOfInterception > START_DATE);
		}
	}

	@Test
	public void shouldNeverGenerateTimestampLaterThanEndDate() {
		for (int i = 0; i < 100; i++) {
			final Timestamp timeOfEvent = generator.generateTimeOfInterception(new Timestamp(START_DATE));
			final Timestamp timeOfInterception = generator.generateTimeOfInterception(timeOfEvent);
			assertTrue(asLong(timeOfEvent) <= END_DATE);
			assertTrue(asLong(timeOfInterception) <= END_DATE);
		}
	}

	@Test
	public void shouldGenerateGeoLocationOnDemand() {
		final GeoLocation geoLocation = generator.generateGeoLocation(GUARANTEED_TO_EXIST);
		assertTrue(geoLocation.longitude <= 180 && geoLocation.longitude >= -180);
		assertTrue(geoLocation.latitude <= 90 && geoLocation.latitude >= -90);
	}

	@Test
	public void shouldNotGenerateGeoLocation() {
		final GeoLocation geoLocation = generator.generateGeoLocation(GUARANTEED_NOT_TO_EXIST);
		assertNull(geoLocation);
	}

	private static long asLong(Date date) {
		return date.getTime();
	}

	private Long asLong(Timestamp timestamp) {
		return timestamp.getTimestamp();
	}

	private static class TestEventGenerator extends EventGenerator<TestEvent> {
		public TestEventGenerator(long randomSeed) {
			super(randomSeed);
		}

		public GeoLocation generateGeoLocation(double probabilityOfExistance) {
			return randomLocation(probabilityOfExistance);
		}

		@Override
		protected TestEvent createInstance() {
			final Timestamp timeOfEvent = generateTimeOfEvent();
			return new TestEvent(timeOfEvent, generateTimeOfInterception(timeOfEvent));
		}
	}

	private static class TestEvent extends Event {
		public TestEvent(Timestamp timeOfEvent, Timestamp timeOfInterception) {
			super(timeOfEvent, timeOfInterception);
		}
	}
}
