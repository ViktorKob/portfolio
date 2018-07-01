package net.thomas.portfolio.shared_objects.analytics;

import static net.thomas.portfolio.shared_objects.analytics.ConfidenceLevel.CERTAIN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AnalyticalKnowledgeUnitTest {
	private static final String ALIAS = "ALIAS";
	private static final ConfidenceLevel KNOWLEDGE = CERTAIN;

	private AnalyticalKnowledge knowledge;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		knowledge = new AnalyticalKnowledge(ALIAS, KNOWLEDGE, KNOWLEDGE);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		final AnalyticalKnowledge deserializedObject = mapper.readValue(serializedForm, AnalyticalKnowledge.class);
		assertEquals(knowledge, deserializedObject);
	}

	@Test
	public void shouldSerializeAsPk_alias() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		assertTrue(serializedForm.contains("\"pk_alias\":\"" + ALIAS + "\""));
	}

	@Test
	public void shouldSerializeAsPk_isKnown() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		assertTrue(serializedForm.contains("\"pk_isKnown\":\"" + KNOWLEDGE.name() + "\""));
	}

	@Test
	public void shouldSerializeAsPk_isRestricted() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		assertTrue(serializedForm.contains("\"pk_isRestricted\":\"" + KNOWLEDGE.name() + "\""));
	}
}