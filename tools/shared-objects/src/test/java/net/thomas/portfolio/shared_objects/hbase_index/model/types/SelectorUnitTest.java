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
		localname = new Selector();
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