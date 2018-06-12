package net.thomas.portfolio.shared_objects.hbase_index.request;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BoundsUnitTest {
	private static final Integer OFFSET = 1;
	private static final Integer LIMIT = 2;
	private static final Long AFTER = 3l;
	private static final Long BEFORE = 4l;

	private Bounds bounds;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		bounds = new Bounds(OFFSET, LIMIT, AFTER, BEFORE);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(bounds);
		final Bounds deserializedObject = mapper.readValue(serializedForm, Bounds.class);
		assertEquals(bounds, deserializedObject);
	}

	@Test
	public void shouldSerializeAsIil_offset() throws IOException {
		final String serializedForm = mapper.writeValueAsString(bounds);
		assertTrue(serializedForm.contains("\"b_offset\":" + OFFSET));
	}

	@Test
	public void shouldSerializeAsIil_limit() throws IOException {
		final String serializedForm = mapper.writeValueAsString(bounds);
		assertTrue(serializedForm.contains("\"b_limit\":" + LIMIT));
	}

	@Test
	public void shouldSerializeAsIil_after() throws IOException {
		final String serializedForm = mapper.writeValueAsString(bounds);
		assertTrue(serializedForm.contains("\"b_after\":" + AFTER));
	}

	@Test
	public void shouldSerializeAsIil_before() throws IOException {
		final String serializedForm = mapper.writeValueAsString(bounds);
		assertTrue(serializedForm.contains("\"b_before\":" + BEFORE));
	}
}
