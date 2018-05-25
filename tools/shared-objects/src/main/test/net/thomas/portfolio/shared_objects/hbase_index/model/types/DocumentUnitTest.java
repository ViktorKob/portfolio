package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DocumentUnitTest {
	private Document email;
	private ObjectMapper mapper;
	private Selector emailAddress;

	@Before
	public void setupForTest() {
		final Selector localname = new Selector();
		localname.setId(new DataTypeId("Localname", "ABCD01"));
		localname.setUid("");
		localname.put("name", "xyz");
		final Selector domain = new Selector();
		domain.setId(new DataTypeId("Domain", "ABCD02"));
		domain.put("name", "xyz.vw");
		emailAddress = new Selector();
		emailAddress.setId(new DataTypeId("EmailAddress", "ABCD03"));
		emailAddress.put("localname", localname);
		emailAddress.put("domain", domain);
		email = new Document();
		email.setId(new DataTypeId("Email", "ABDC05"));
		email.put("subject", "subject");
		email.setTimeOfEvent(1l);
		email.setTimeOfInterception(2l);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldDeserializeSimpleDocumentCorrectlyWithoutSubTypes() throws IOException {
		final String serializedInstance = mapper.writeValueAsString(email);
		final Document deserializedInstance = mapper.readValue(serializedInstance, Document.class);
		assertEquals(email, deserializedInstance);
	}

	@Test
	public void shouldDeserializeSimpleDocumentCorrectlyWithSender() throws IOException {
		email.put("from", emailAddress);
		final String serializedInstance = mapper.writeValueAsString(email);
		final Document deserializedInstance = mapper.readValue(serializedInstance, Document.class);
		assertEquals(email, deserializedInstance);
	}

	@Test
	public void shouldDeserializeSimpleDocumentCorrectlyWithRecipients() throws IOException {
		email.put("to", asList(emailAddress, emailAddress));
		final String serializedInstance = mapper.writeValueAsString(email);
		final Document deserializedInstance = mapper.readValue(serializedInstance, Document.class);
		assertEquals(email, deserializedInstance);
	}
}
