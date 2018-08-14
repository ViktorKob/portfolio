package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.createArrayField;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.createNonKeyArrayField;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.decimal;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.geoLocation;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.isPrimitiveClass;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.nonKeyDecimal;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.nonKeyInteger;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.nonKeyString;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.strings;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.timestamp;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.DECIMAL;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.GEO_LOCATION;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.INTEGER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.STRING;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.TIMESTAMP;
import static net.thomas.portfolio.testing_tools.EqualsTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.HashCodeTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class PrimitiveFieldUnitTest {
	private PrimitiveField field;

	@Before
	public void setup() {
		field = strings(SOME_NAME);
	}

	@Test
	public void shouldSayStringIsPrimitive() {
		assertTrue(isPrimitiveClass(String.class));
	}

	@Test
	public void shouldSayIntegersArePrimitive() {
		assertTrue(isPrimitiveClass(Integer.class));
		assertTrue(isPrimitiveClass(Long.class));
		assertTrue(isPrimitiveClass(Short.class));
		assertTrue(isPrimitiveClass(Byte.class));
	}

	@Test
	public void shouldSayDecimalsArePrimitive() {
		assertTrue(isPrimitiveClass(Float.class));
		assertTrue(isPrimitiveClass(Double.class));
	}

	@Test
	public void shouldSayTimestampIsPrimitive() {
		assertTrue(isPrimitiveClass(Timestamp.class));
	}

	@Test
	public void shouldSayGeoLocationIsPrimitive() {
		assertTrue(isPrimitiveClass(GeoLocation.class));
	}

	@Test
	public void checkValuesForStringsField() {
		assertEquals(SOME_NAME, field.getName());
		assertEquals(STRING, field.getType());
		assertTrue(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForStringField() {
		field = PrimitiveField.string(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(STRING, field.getType());
		assertFalse(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForIntegerField() {
		field = PrimitiveField.integer(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(INTEGER, field.getType());
		assertFalse(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForDecimalField() {
		field = decimal(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(DECIMAL, field.getType());
		assertFalse(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForTimestampField() {
		field = timestamp(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(TIMESTAMP, field.getType());
		assertFalse(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForGeoLocationField() {
		field = geoLocation(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(GEO_LOCATION, field.getType());
		assertFalse(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyStringField() {
		field = nonKeyString(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(STRING, field.getType());
		assertFalse(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyIntegerField() {
		field = nonKeyInteger(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(INTEGER, field.getType());
		assertFalse(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyDecimalField() {
		field = nonKeyDecimal(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(DECIMAL, field.getType());
		assertFalse(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyTimestampField() {
		field = PrimitiveField.nonKeyTimestamp(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(TIMESTAMP, field.getType());
		assertFalse(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyGeoLocationField() {
		field = PrimitiveField.nonKeyGeoLocation(SOME_NAME);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(GEO_LOCATION, field.getType());
		assertFalse(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyField() {
		field = PrimitiveField.createNonKeyField(SOME_NAME, STRING);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(STRING, field.getType());
		assertFalse(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForArrayField() {
		field = createArrayField(SOME_NAME, STRING);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(STRING, field.getType());
		assertTrue(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyArrayField() {
		field = createNonKeyArrayField(SOME_NAME, STRING);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(STRING, field.getType());
		assertTrue(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForFieldBuilder() {
		field = new PrimitiveField.PrimitiveFieldBuilder(SOME_NAME).setType(String.class)
			.markIsArray()
			.markIsPartOfKey()
			.build();
		assertEquals(SOME_NAME, field.getName());
		assertEquals(STRING, field.getType());
		assertTrue(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(string(SOME_NAME));
		assertCanSerializeAndDeserialize(strings(SOME_NAME));
		assertCanSerializeAndDeserialize(decimal(SOME_NAME));
		assertCanSerializeAndDeserialize(geoLocation(SOME_NAME));
		assertCanSerializeAndDeserialize(timestamp(SOME_NAME));
	}

	@Test
	public void shouldSurviveNullParameters() {
		assertCanSerializeAndDeserializeWithNullValues(field);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValidIncludingNullChecks(field);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEqualsIsValidIncludingNullChecks(field);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(field);
	}

	private static final String SOME_NAME = "Some name";
}