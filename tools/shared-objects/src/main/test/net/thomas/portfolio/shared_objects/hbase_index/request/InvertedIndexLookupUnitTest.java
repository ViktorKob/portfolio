package net.thomas.portfolio.shared_objects.hbase_index.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

public class InvertedIndexLookupUnitTest {
	private static final DataTypeId ID = new DataTypeId("TYPE", "ABCD");
	private static final Integer OFFSET = 1;
	private static final Integer LIMIT = 2;
	private static final Long AFTER = 3l;
	private static final Long BEFORE = 4l;

	private InvertedIndexLookup lookup;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		lookup = new InvertedIndexLookup(ID, OFFSET, LIMIT, AFTER, BEFORE);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(lookup);
		final InvertedIndexLookup deserializedObject = mapper.readValue(serializedForm, InvertedIndexLookup.class);
		assertEquals(lookup, deserializedObject);
	}

	@Test
	public void shouldSerializeAsIil_offset() throws IOException {
		final String serializedForm = mapper.writeValueAsString(lookup);
		assertTrue(serializedForm.contains("\"iil_offset\":" + OFFSET));
	}

	@Test
	public void shouldSerializeAsIil_limit() throws IOException {
		final String serializedForm = mapper.writeValueAsString(lookup);
		assertTrue(serializedForm.contains("\"iil_limit\":" + LIMIT));
	}

	@Test
	public void shouldSerializeAsIil_after() throws IOException {
		final String serializedForm = mapper.writeValueAsString(lookup);
		assertTrue(serializedForm.contains("\"iil_after\":" + AFTER));
	}

	@Test
	public void shouldSerializeAsIil_before() throws IOException {
		final String serializedForm = mapper.writeValueAsString(lookup);
		assertTrue(serializedForm.contains("\"iil_before\":" + BEFORE));
	}
}
