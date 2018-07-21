package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.thomas.portfolio.common.services.parameters.Parameter;

public class DataTypeIdUnitTest {

	@Test
	public void shouldBeEqual() {
		assertTrue(SOME_ID.equals(SOME_ID));
	}

	@Test
	public void shouldNotBeEqualWithDifferentType() {
		assertFalse(SOME_ID.equals(SOME_ID_WITH_DIFFERENT_TYPE));
	}

	@Test
	public void shouldNotBeEqualWithDifferentUid() {
		assertFalse(SOME_ID.equals(SOME_ID_WITH_LOWER_CASE_UID));
	}

	@Test
	public void shouldNotBeEqualWithDifferentObject() {
		assertFalse(SOME_ID.equals(ANOTHER_OBJECT));
	}

	@Test
	public void shouldHaveSameHashCode() {
		assertEquals(SOME_ID.hashCode(), SOME_ID.hashCode());
	}

	@Test
	public void shouldNotHaveSameHashCode() {
		assertNotEquals(SOME_ID.hashCode(), SOME_ID_WITH_LOWER_CASE_UID.hashCode());
	}

	@Test
	public void shouldSerializeAndDeserialize() {
		final DataTypeId deserializedObject = serializeDeserialize(SOME_ID, DataTypeId.class);
		assertEquals(SOME_ID, deserializedObject);
	}

	@Test
	public void shouldCopyIdCorrectly() {
		final DataTypeId copy = new DataTypeId(SOME_ID);
		assertEquals(SOME_ID, copy);
	}

	@Test
	public void shouldTolerateNullUid() {
		new DataTypeId(TYPE, null);
	}

	@Test
	public void shouldTolerateNullUidDuringEquals() {
		final DataTypeId partialId = new DataTypeId(TYPE, null);
		partialId.equals(SOME_ID);
	}

	@Test
	public void shouldUpperCaseUidDuringDeserialization() throws IOException {
		final DataTypeId deserializedObject = serializeDeserialize(SOME_ID_WITH_LOWER_CASE_UID, DataTypeId.class);
		assertEquals(SOME_ID, deserializedObject);
	}

	@Test
	public void shouldContainTypeParameter() {
		final Parameter[] parameters = SOME_ID.getParameters();
		boolean found = false;
		for (final Parameter parameter : parameters) {
			found |= SOME_ID.type.equals(parameter.getValue());
		}
		assertTrue(found);
	}

	@Test
	public void shouldContainUidParameter() throws IOException {
		final Parameter[] parameters = SOME_ID.getParameters();
		boolean found = false;
		for (final Parameter parameter : parameters) {
			found |= SOME_ID.uid.equals(parameter.getValue());
		}
		assertTrue(found);
	}

	private static final String TYPE = "TYPE";
	private static final DataTypeId SOME_ID = new DataTypeId(TYPE, "ABCD");
	private static final DataTypeId SOME_ID_WITH_DIFFERENT_TYPE = new DataTypeId("AnotherType", SOME_ID.uid);
	private static final DataTypeId SOME_ID_WITH_LOWER_CASE_UID = new DataTypeId();
	private static final Object ANOTHER_OBJECT = "object";

	static {
		SOME_ID_WITH_LOWER_CASE_UID.type = TYPE;
		SOME_ID_WITH_LOWER_CASE_UID.uid = SOME_ID.uid.toLowerCase();
	}
}