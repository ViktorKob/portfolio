package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertCanSerializeAndDeserializeWithNullValues;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertEqualsIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertHashCodeIsValidIncludingNullChecks;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.assertParametersMatchParameterGroups;
import static net.thomas.portfolio.shared_objects.test_utils.ProtocolTestUtil.serializeDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class DataTypeIdUnitTest {
	@Test
	public void shouldNotBeEqualWithDifferentUid() {
		assertFalse(SOME_ID.equals(SOME_ID_WITH_LOWER_CASE_UID));
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
	public void shouldUpperCaseUidDuringDeserialization() throws IOException {
		final DataTypeId deserializedObject = serializeDeserialize(SOME_ID_WITH_LOWER_CASE_UID);
		assertEquals(SOME_ID, deserializedObject);
	}

	@Test
	public void shouldHaveSymmetricProtocol() {
		assertCanSerializeAndDeserialize(SOME_ID);
	}

	@Test
	public void shouldSurviveNullParameters() {
		assertCanSerializeAndDeserializeWithNullValues(SOME_ID);
	}

	@Test
	public void shouldMatchParameterGroup() {
		assertParametersMatchParameterGroups(SOME_ID);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertHashCodeIsValidIncludingNullChecks(SOME_ID);
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEqualsIsValidIncludingNullChecks(SOME_ID);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		final String asString = SOME_ID.toString();
		assertTrue(asString.contains(SOME_ID.type));
		assertTrue(asString.contains(SOME_ID.uid));
	}

	private static final String TYPE = "TYPE";
	private static final DataTypeId SOME_ID = new DataTypeId(TYPE, "ABCD");
	private static final DataTypeId SOME_ID_WITH_LOWER_CASE_UID = new DataTypeId();

	static {
		SOME_ID_WITH_LOWER_CASE_UID.type = TYPE;
		SOME_ID_WITH_LOWER_CASE_UID.uid = SOME_ID.uid.toLowerCase();
	}
}