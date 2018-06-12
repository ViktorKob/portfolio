package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DataTypeIdUnitTest {
	private static final String TYPE = "TYPE";
	private static final String UID = "ABCD";

	private DataTypeId id;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		id = new DataTypeId(TYPE, UID);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(id);
		final DataTypeId deserializedObject = mapper.readValue(serializedForm, DataTypeId.class);
		assertEquals(id, deserializedObject);
	}

	@Test
	public void shouldSerializeAsDti_type() throws IOException {
		final String serializedForm = mapper.writeValueAsString(id);
		assertTrue(serializedForm.contains("\"dti_type\":\"" + TYPE + "\""));
	}

	@Test
	public void shouldSerializeAsDti_uid() throws IOException {
		final String serializedForm = mapper.writeValueAsString(id);
		assertTrue(serializedForm.contains("\"dti_uid\":\"" + UID + "\""));
	}
}
