package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertBasicEqualsIsValid;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValid;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
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
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(SOME_LOCATION);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValid(SOME_LOCATION);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertBasicEqualsIsValid(SOME_LOCATION);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(SOME_LOCATION);
	}

	private static final double SOME_LONGITUDE = 1.0d;
	private static final double SOME_LATITUDE = 2.0d;
	private static final GeoLocation SOME_LOCATION = new GeoLocation(SOME_LONGITUDE, SOME_LATITUDE);
	private static final double SOME_OTHER_LONGITUDE = 3.0d;
	private static final double SOME_OTHER_LATITUDE = 4.0d;
	private static final GeoLocation OTHER_GEO_LOCATION = new GeoLocation(SOME_OTHER_LONGITUDE, SOME_OTHER_LATITUDE);
	private static final Object ANOTHER_OBJECT = "object";
}