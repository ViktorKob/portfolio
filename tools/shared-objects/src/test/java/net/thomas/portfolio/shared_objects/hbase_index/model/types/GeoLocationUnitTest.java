package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.lang.String.valueOf;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoLocationUnitTest {
	@Test
	public void shouldBeEqual() {
		assertTrue(SOME_LOCATION.equals(SOME_LOCATION));
	}

	@Test
	public void shouldNotBeEqualWithDifferentLongitude() {
		final GeoLocation differentLocation = new GeoLocation(SOME_LOCATION.longitude, SOME_OTHER_LATITUDE);
		assertFalse(SOME_LOCATION.equals(differentLocation));
	}

	@Test
	public void shouldNotBeEqualWithDifferentLatitude() {
		final GeoLocation differentLocation = new GeoLocation(SOME_OTHER_LONGITUDE, SOME_LOCATION.latitude);
		assertFalse(SOME_LOCATION.equals(differentLocation));
	}

	@Test
	public void shouldNotBeEqualWithDifferentObject() {
		assertFalse(SOME_LOCATION.equals(ANOTHER_OBJECT));
	}

	@Test
	public void shouldHaveSameHashCode() {
		assertEquals(SOME_LOCATION.hashCode(), SOME_LOCATION.hashCode());
	}

	@Test
	public void shouldNotHaveSameHashCode() {
		assertNotEquals(SOME_LOCATION.hashCode(), OTHER_GEO_LOCATION.hashCode());
	}

	@Test
	public void shouldSerializeAndDeserializeGeoLocationCorrectly() {
		final GeoLocation deserializedInstance = serializeDeserialize(SOME_LOCATION, GeoLocation.class);
		assertEquals(SOME_LOCATION, deserializedInstance);
	}

	@Test
	public void shouldContainLongitude() {
		final String longitude = valueOf(SOME_LOCATION.longitude);
		final String asString = SOME_LOCATION.toString();
		assertTrue(asString.contains(longitude));
	}

	@Test
	public void shouldContainLatitude() {
		final String latitude = valueOf(SOME_LOCATION.latitude);
		final String asString = SOME_LOCATION.toString();
		assertTrue(asString.contains(latitude));
	}

	private static final double SOME_LONGITUDE = 1.0d;
	private static final double SOME_LATITUDE = 2.0d;
	private static final GeoLocation SOME_LOCATION = new GeoLocation(SOME_LONGITUDE, SOME_LATITUDE);
	private static final double SOME_OTHER_LONGITUDE = 3.0d;
	private static final double SOME_OTHER_LATITUDE = 4.0d;
	private static final GeoLocation OTHER_GEO_LOCATION = new GeoLocation(SOME_OTHER_LONGITUDE, SOME_OTHER_LATITUDE);
	private static final Object ANOTHER_OBJECT = "object";
}