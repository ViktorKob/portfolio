package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.DomainSimpleRepParser.newDomainParser;
import static net.thomas.portfolio.shared_objects.test_utils.DataTypeFieldMatcher.matchesField;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserLibrary;

public class DomainSimpleRepParserUnitTest {

	private DomainSimpleRepParser parser;
	private IdCalculator idCalculatorMock;

	@Before
	public void setupForTest() {
		idCalculatorMock = mock(IdCalculator.class);
		parser = newDomainParser(idCalculatorMock);
	}

	@Test
	public void shouldParseTopLevelSimpleRepAndBuildDomain() {
		when(idCalculatorMock.calculate(eq(DOMAIN_TYPE), argThat(matchesField(VALUE_FIELD, TOP_LEVEL_DOMAIN_SIMPLE_REP)))).thenReturn(ID);
		final Selector selector = parser.parse(DOMAIN_TYPE, TOP_LEVEL_DOMAIN_SIMPLE_REP);
		assertEquals(ID, selector.getId());
	}

	@Test
	public void shouldParseSimpleRepAndBuildDomain() {
		when(idCalculatorMock.calculate(eq(DOMAIN_TYPE), argThat(matchesDomainFields(DOMAIN_SIMPLE_REP)))).thenReturn(ID);
		setupLibraryMockWithTopLevelDomainStub(DOMAIN_SIMPLE_REP);
		final Selector selector = parser.parse(DOMAIN_TYPE, DOMAIN_SIMPLE_REP);
		assertEquals(ID, selector.getId());
	}

	private static final String DOMAIN_TYPE = "Domain";
	private static final String VALUE_FIELD = "domainPart";
	private static final String REFERENCE_FIELD = "domain";
	private static final String UID = "AA";
	private static final String TOP_LEVEL_DOMAIN_SIMPLE_REP = "AB";
	private static final Selector TOP_LEVEL_ENTITY_STUB = new Selector();
	private static final String DOMAIN_SIMPLE_REP = "ABCD.AB";
	private static final DataTypeId ID = new DataTypeId(DOMAIN_TYPE, UID);

	private void setupLibraryMockWithTopLevelDomainStub(String domainSimpleRep) {
		final SimpleRepresentationParserLibrary libraryMock = mock(SimpleRepresentationParserLibrary.class);
		when(libraryMock.parse(DOMAIN_TYPE, domainSimpleRep)).thenReturn(TOP_LEVEL_ENTITY_STUB);
		parser.setLibrary(libraryMock);
	}

	private ArgumentMatcher<DataType> matchesDomainFields(String domainSimpleRep) {
		return (entity) -> {
			final String[] parts = domainSimpleRep.split("\\.");
			if (!parts[0].equals(entity.get(VALUE_FIELD)) || TOP_LEVEL_ENTITY_STUB.equals(entity.get(REFERENCE_FIELD))) {
				return false;
			} else {
				return true;
			}
		};
	}
}
