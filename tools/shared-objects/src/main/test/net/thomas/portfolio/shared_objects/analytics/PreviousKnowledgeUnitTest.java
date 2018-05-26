package net.thomas.portfolio.shared_objects.analytics;

import static net.thomas.portfolio.shared_objects.analytics.RecognitionLevel.KNOWN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PreviousKnowledgeUnitTest {
	private static final String ALIAS = "ALIAS";
	private static final RecognitionLevel KNOWLEDGE = KNOWN;

	private PreviousKnowledge knowledge;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		knowledge = new PreviousKnowledge(ALIAS, KNOWLEDGE, KNOWLEDGE);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		System.out.println(serializedForm);
		final PreviousKnowledge deserializedObject = mapper.readValue(serializedForm, PreviousKnowledge.class);
		assertEquals(knowledge, deserializedObject);
	}

	@Test
	public void shouldSerializeAsPk_alias() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		assertTrue(serializedForm.contains("\"pk_alias\":\"" + ALIAS + "\""));
	}

	@Test
	public void shouldSerializeAsPk_recognition() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		assertTrue(serializedForm.contains("\"pk_recognition\":\"" + KNOWLEDGE.name() + "\""));
	}

	@Test
	public void shouldSerializeAsPk_isDanish() throws IOException {
		final String serializedForm = mapper.writeValueAsString(knowledge);
		assertTrue(serializedForm.contains("\"pk_isDanish\":\"" + KNOWLEDGE.name() + "\""));
	}
}
