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
	private RawDataType emailEndpoint;

	@Before
	public void setupForTest() {
		final Selector localname = new Selector();
		localname.setId(new DataTypeId("Localname", "ABCD01"));
		localname.put("name", "xyz");
		final Selector topLevelDomain = new Selector();
		topLevelDomain.setId(new DataTypeId("Domain", "ABCD02"));
		topLevelDomain.put("domainPart", "vw");
		final Selector domain = new Selector();
		domain.setId(new DataTypeId("Domain", "ABCD03"));
		domain.put("domainPart", "xyz");
		domain.put("domain", topLevelDomain);
		emailAddress = new Selector();
		emailAddress.setId(new DataTypeId("EmailAddress", "ABCD04"));
		emailAddress.put("localname", localname);
		emailAddress.put("domain", domain);
		emailEndpoint = new RawDataType();
		emailEndpoint.setId(new DataTypeId("EmailEndpoint", "ABCD05"));
		emailEndpoint.put("address", emailAddress);
		email = new Document();
		email.setId(new DataTypeId("Email", "ABDC06"));
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
		email.put("from", emailEndpoint);
		final String serializedInstance = mapper.writeValueAsString(email);
		final Document deserializedInstance = mapper.readValue(serializedInstance, Document.class);
		assertEquals(email, deserializedInstance);
	}

	@Test
	public void shouldDeserializeSimpleDocumentCorrectlyWithRecipients() throws IOException {
		email.put("to", asList(emailEndpoint, emailEndpoint));
		final String serializedInstance = mapper.writeValueAsString(email);
		final Document deserializedInstance = mapper.readValue(serializedInstance, Document.class);
		assertEquals(email, deserializedInstance);
	}
}
