package net.thomas.portfolio.shared_objects.legal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LegalInformationUnitTest {
	private static final String USER = "USER";
	private static final String JUSTIFICATION = "JUSTIFICATION";
	private static final Long LOWER_BOUND = 3l;
	private static final Long UPPER_BOUND = 4l;

	private LegalInformation info;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		info = new LegalInformation(USER, JUSTIFICATION, LOWER_BOUND, UPPER_BOUND);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(info);
		final LegalInformation deserializedObject = mapper.readValue(serializedForm, LegalInformation.class);
		assertEquals(info, deserializedObject);
	}

	@Test
	public void shouldSerializeAsLi_user() throws IOException {
		final String serializedForm = mapper.writeValueAsString(info);
		assertTrue(serializedForm.contains("\"li_user\":\"" + USER + "\""));
	}

	@Test
	public void shouldSerializeAsLi_justification() throws IOException {
		final String serializedForm = mapper.writeValueAsString(info);
		assertTrue(serializedForm.contains("\"li_justification\":\"" + JUSTIFICATION + "\""));
	}

	@Test
	public void shouldSerializeAsLi_lowerBound() throws IOException {
		final String serializedForm = mapper.writeValueAsString(info);
		assertTrue(serializedForm.contains("\"li_lowerBound\":" + LOWER_BOUND));
	}

	@Test
	public void shouldSerializeAsLi_upperBound() throws IOException {
		final String serializedForm = mapper.writeValueAsString(info);
		assertTrue(serializedForm.contains("\"li_upperBound\":" + UPPER_BOUND));
	}
}
