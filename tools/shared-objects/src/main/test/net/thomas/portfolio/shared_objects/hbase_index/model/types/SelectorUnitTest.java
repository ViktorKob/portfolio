package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SelectorUnitTest {
	private Selector localname;
	private Selector emailAddress;
	private ObjectMapper mapper;

	@Before
	public void setupForTest() {
		localname = new Selector("Localname");
		localname.setUid("ABCD01");
		final Selector domain = new Selector("Domain");
		domain.setUid("ABCD02");
		emailAddress = new Selector("EmailAddress");
		emailAddress.setUid("ABCD03");
		emailAddress.put("localname", localname);
		emailAddress.put("domain", domain);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldDeserializeSimpleSelectorCorrectly() throws IOException {
		final String serializedInstance = mapper.writeValueAsString(localname);
		final Selector deserializedInstance = mapper.readValue(serializedInstance, Selector.class);
		assertEquals(localname, deserializedInstance);
	}

	@Test
	public void shouldDeserializeComplexSelectorCorrectly() throws IOException {
		final String serializedInstance = mapper.writeValueAsString(emailAddress);
		final Selector deserializedInstance = mapper.readValue(serializedInstance, Selector.class);
		assertEquals(emailAddress, deserializedInstance);
	}
}
