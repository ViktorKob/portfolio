package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.EmailAddressSimpleRepParser.newEmailAddressParser;
import static net.thomas.portfolio.shared_objects.test_utils.DataTypeFieldMatcher.matchesFields;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserLibrary;

public class EmailAddressSimpleRepParserUnitTest {

	private EmailAddressSimpleRepParser parser;

	@Before
	public void setUpForTest() {
		final IdCalculator idCalculatorMock = mock(IdCalculator.class);
		final Map<String, Object> expectedFields = createExpectedEmailAddressFields();
		when(idCalculatorMock.calculate(eq(EMAIL_ADDRESS_TYPE), argThat(matchesFields(expectedFields)))).thenReturn(ID);
		parser = newEmailAddressParser(idCalculatorMock);
		parser.setLibrary(setUpLibraryMock());
	}

	@Test
	public void shouldParseEmailAddress() {
		final Selector selector = parser.parse(EMAIL_ADDRESS_TYPE, EMAIL_ADDRESS_SIMPLE_REP);
		assertEquals(ID, selector.getId());
	}

	private static final String LOCALNAME_TYPE = "Localname";
	private static final String LOCALNAME_SIMPLE_REP = "ABCD";
	private static final Selector LOCALNAME_ENTITY_STUB = new Selector();
	private static final String DOMAIN_TYPE = "Domain";
	private static final String DOMAIN_SIMPLE_REP = "ABCD.AB";
	private static final Selector DOMAIN_ENTITY_STUB = new Selector();
	private static final String DOMAIN_FIELD = "localname";
	private static final String LOCALNAME_FIELD = "domain";
	private static final String EMAIL_ADDRESS_TYPE = "EmailAddress";
	private static final String EMAIL_ADDRESS_SIMPLE_REP = LOCALNAME_SIMPLE_REP + "@" + DOMAIN_SIMPLE_REP;
	private static final String UID = "AA";
	private static final DataTypeId ID = new DataTypeId(EMAIL_ADDRESS_TYPE, UID);

	private Map<String, Object> createExpectedEmailAddressFields() {
		final Map<String, Object> fields = new HashMap<>();
		fields.put(LOCALNAME_FIELD, LOCALNAME_ENTITY_STUB);
		fields.put(DOMAIN_FIELD, DOMAIN_ENTITY_STUB);
		return fields;
	}

	private SimpleRepresentationParserLibrary setUpLibraryMock() {
		final SimpleRepresentationParserLibrary libraryMock = mock(SimpleRepresentationParserLibrary.class);
		when(libraryMock.parse(eq(LOCALNAME_TYPE), eq(LOCALNAME_SIMPLE_REP))).thenReturn(LOCALNAME_ENTITY_STUB);
		when(libraryMock.parse(eq(DOMAIN_TYPE), eq(DOMAIN_SIMPLE_REP))).thenReturn(DOMAIN_ENTITY_STUB);
		return libraryMock;
	}
}
