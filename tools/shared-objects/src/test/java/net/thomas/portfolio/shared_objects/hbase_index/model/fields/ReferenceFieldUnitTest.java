package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataTypeArray;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.nonKeyDataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.nonKeyDataTypeArray;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ReferenceFieldUnitTest {
	private ReferenceField field;

	@Before
	public void setup() {
		field = dataType(SOME_NAME, SOME_TYPE);
	}

	@Test
	public void checkValuesForDefaultField() {
		assertEquals(SOME_NAME, field.getName());
		assertEquals(SOME_TYPE, field.getType());
		assertFalse(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyField() {
		field = nonKeyDataType(SOME_NAME, SOME_TYPE);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(SOME_TYPE, field.getType());
		assertFalse(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForArrayField() {
		field = dataTypeArray(SOME_NAME, SOME_TYPE);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(SOME_TYPE, field.getType());
		assertTrue(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void checkValuesForNonKeyArrayField() {
		field = nonKeyDataTypeArray(SOME_NAME, SOME_TYPE);
		assertEquals(SOME_NAME, field.getName());
		assertEquals(SOME_TYPE, field.getType());
		assertTrue(field.isArray());
		assertFalse(field.isKeyComponent());
	}

	@Test
	public void checkValuesForFieldBuilder() {
		field = new ReferenceField.ReferenceFieldBuilder(SOME_NAME).setType(SOME_TYPE)
			.markIsArray()
			.markIsPartOfKey()
			.build();
		assertEquals(SOME_NAME, field.getName());
		assertEquals(SOME_TYPE, field.getType());
		assertTrue(field.isArray());
		assertTrue(field.isKeyComponent());
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(dataType(SOME_NAME, SOME_TYPE));
		assertCanSerializeAndDeserialize(dataTypeArray(SOME_NAME, SOME_TYPE));
		assertCanSerializeAndDeserialize(nonKeyDataType(SOME_NAME, SOME_TYPE));
		assertCanSerializeAndDeserialize(nonKeyDataTypeArray(SOME_NAME, SOME_TYPE));
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
	private static final String SOME_TYPE = "Some type";
}
