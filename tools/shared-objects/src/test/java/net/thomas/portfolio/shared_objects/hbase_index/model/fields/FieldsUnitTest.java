package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.strings;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class FieldsUnitTest {
	private FieldsBuilder builder;

	@Before
	public void setUpForTest() {
		builder = new FieldsBuilder();
	}

	@Test
	public void shouldContainFieldUsingBuilder() {
		builder.add(string(SOME_NAME));
		assertIsOnlyElement(SOME_NAME, builder.build());
	}

	@Test
	public void shouldContainFieldUsingArrayInstancingFunction() {
		final Fields fields = fields(string(SOME_NAME));
		assertIsOnlyElement(SOME_NAME, fields);
	}

	@Test
	public void shouldContainFieldUsingMapInstancingFunction() {
		final Fields fields = fields(singletonMap(SOME_NAME, string(SOME_NAME)));
		assertIsOnlyElement(SOME_NAME, fields);
	}

	@Test
	public void shouldContainFieldWithArrayUsingMapInstancingFunction() {
		final Fields fields = fields(singletonMap(SOME_NAME, strings(SOME_NAME)));
		assertIsOnlyElement(SOME_NAME, fields);
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(fields(singletonMap(SOME_NAME, string(SOME_NAME))));
		assertCanSerializeAndDeserialize(fields(singletonMap(SOME_NAME, strings(SOME_NAME))));
	}

	private void assertIsOnlyElement(String fieldName, Fields fields) {
		final Field field = only(fields);
		assertEquals(fieldName, field.getName());
	}

	private Field only(Fields fields) {
		final Iterator<Field> iterator = fields.iterator();
		final Field element = iterator.next();
		assertFalse(iterator.hasNext());
		return element;
	}

	private static final String SOME_NAME = "SOME_NAME";
}