package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RawDataTypeUnitTest {
	private ObjectMapper mapper;
	private Selector emailAddress;
	private RawDataType emailEndpoint;
	private Selector displayedName;

	@Before
	public void setupForTest() {
		displayedName = new Selector();
		displayedName.setId(new DataTypeId("DisplayedName", "ABCD01"));
		displayedName.put("name", "xyz");
		final Selector localname = new Selector();
		localname.setId(new DataTypeId("Localname", "ABCD02"));
		localname.put("name", "xyz");
		final Selector topLevelDomain = new Selector();
		topLevelDomain.setId(new DataTypeId("Domain", "ABCD03"));
		topLevelDomain.put("domainPart", "vw");
		final Selector domain = new Selector();
		domain.setId(new DataTypeId("Domain", "ABCD04"));
		domain.put("domainPart", "xyz");
		domain.put("domain", topLevelDomain);
		emailAddress = new Selector();
		emailAddress.setId(new DataTypeId("EmailAddress", "ABCD05"));
		emailAddress.put("localname", localname);
		emailAddress.put("domain", domain);
		emailEndpoint = new RawDataType();
		emailEndpoint.setId(new DataTypeId("EmailEndpoint", "ABCD06"));
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldDeserializeEndpointCorrectly() throws IOException {
		final String serializedInstance = mapper.writeValueAsString(emailEndpoint);
		final RawDataType deserializedInstance = mapper.readValue(serializedInstance, RawDataType.class);
		assertEquals(emailEndpoint, deserializedInstance);
	}

	@Test
	public void shouldDeserializeEndpointWithValuesCorrectly() throws IOException {
		emailEndpoint.put("displayedName", displayedName);
		emailEndpoint.put("address", emailAddress);
		final String serializedInstance = mapper.writeValueAsString(emailEndpoint);
		final RawDataType deserializedInstance = mapper.readValue(serializedInstance, RawDataType.class);
		assertEquals(emailEndpoint, deserializedInstance);
	}
}
