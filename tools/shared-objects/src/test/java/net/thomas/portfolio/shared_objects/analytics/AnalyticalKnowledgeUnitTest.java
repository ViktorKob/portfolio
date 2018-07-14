package net.thomas.portfolio.shared_objects.analytics;

import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.POSSIBLY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnalyticalKnowledgeUnitTest {
	private static final String ALIAS = "ALIAS";
	private static final AnalyticalKnowledge SOME_KNOWLEDGE = new AnalyticalKnowledge(ALIAS, CERTAIN, CERTAIN);
	private static final AnalyticalKnowledge SOME_OTHER_KNOWLEDGE = new AnalyticalKnowledge(ALIAS, POSSIBLY, POSSIBLY);
	private static final Object ANOTHER_OBJECT = "object";

	private ObjectMapper mapper;

	@Before
	public void setup() {
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldBeEqual() throws IOException {
		assertTrue(SOME_KNOWLEDGE.equals(SOME_KNOWLEDGE));
	}

	@Test
	public void shouldNotBeEqualWithDifferentValues() throws IOException {
		assertFalse(SOME_KNOWLEDGE.equals(SOME_OTHER_KNOWLEDGE));
	}

	@Test
	public void shouldNotBeEqualWithDifferentObject() throws IOException {
		assertFalse(SOME_KNOWLEDGE.equals(ANOTHER_OBJECT));
	}

	@Test
	public void shouldHaveSameHashCode() throws IOException {
		assertEquals(SOME_KNOWLEDGE.hashCode(), SOME_KNOWLEDGE.hashCode());
	}

	@Test
	public void shouldNotHaveSameHashCode() throws IOException {
		assertNotEquals(SOME_KNOWLEDGE.hashCode(), SOME_OTHER_KNOWLEDGE.hashCode());
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(SOME_KNOWLEDGE);
		final AnalyticalKnowledge deserializedObject = mapper.readValue(serializedForm, AnalyticalKnowledge.class);
		assertEquals(SOME_KNOWLEDGE, deserializedObject);
	}
}