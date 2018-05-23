package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType.RAW;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

public class DocumentUnitTest {
	private Document email;
	private ObjectMapper mapper;

	@Before
	public void setupForTest() {
		final Selector localname = new Selector("Localname");
		localname.setUid("ABCD01");
		final Selector domain = new Selector("Domain");
		domain.setUid("ABCD02");
		final Selector emailAddress = new Selector("EmailAddress");
		emailAddress.setUid("ABCD03");
		emailAddress.put("localname", localname);
		emailAddress.put("domain", domain);
		final DataType emailEndpoint = new DataType(RAW, "EmailEndpoint");
		emailEndpoint.setUid("ABDC04");
		emailEndpoint.put("address", emailEndpoint);
		email = new Document("Email");
		email.setUid("ABDC05");
		email.put("subject", "subject");
		email.setTimeOfEvent(1l);
		email.setTimeOfInterception(2l);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldDeserializeSimpleDocumentCorrectly() throws IOException {
		final String serializedInstance = mapper.writeValueAsString(email);
		final Document deserializedInstance = mapper.readValue(serializedInstance, Document.class);
		assertEquals(email, deserializedInstance);
	}
}
