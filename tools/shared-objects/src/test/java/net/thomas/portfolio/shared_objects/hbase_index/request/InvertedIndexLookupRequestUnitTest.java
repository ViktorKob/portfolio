package net.thomas.portfolio.shared_objects.hbase_index.request;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class InvertedIndexLookupRequestUnitTest {
	private static final DataTypeId ID = new DataTypeId("TYPE", "ABCD");
	private static final LegalInformation LEGAL_INFO = new LegalInformation("USER", "JUSTIFICATION", 1l, 2l);
	private static final Bounds BOUNDS = new Bounds(3, 4, 5l, 6l);
	private static final Set<String> DOCUMENT_TYPES = singleton("DOCUMENT_TYPE");
	private static final Set<String> RELATIONS = singleton("RELATION");

	private InvertedIndexLookupRequest lookup;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		lookup = new InvertedIndexLookupRequest(ID, LEGAL_INFO, BOUNDS, DOCUMENT_TYPES, RELATIONS);
		mapper = new ObjectMapper();
	}

	@Test
	public void shouldSerializeAndDeserialize() throws IOException {
		final String serializedForm = mapper.writeValueAsString(lookup);
		final InvertedIndexLookupRequest deserializedObject = mapper.readValue(serializedForm, InvertedIndexLookupRequest.class);
		assertEquals(lookup, deserializedObject);
	}
}