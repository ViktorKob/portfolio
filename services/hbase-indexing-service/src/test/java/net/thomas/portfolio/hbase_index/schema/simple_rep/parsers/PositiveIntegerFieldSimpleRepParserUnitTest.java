package net.thomas.portfolio.hbase_index.schema.simple_rep.parsers;

import static net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.PositiveIntegerFieldSimpleRepParser.newPositiveIntegerFieldParser;
import static net.thomas.portfolio.hbase_index.test_utils.DataTypeFieldMatcher.matchesField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class PositiveIntegerFieldSimpleRepParserUnitTest {

	private PositiveIntegerFieldSimpleRepParser parser;

	@Before
	public void setupForTest() {
		final IdCalculator idCalculatorMock = mock(IdCalculator.class);
		onlyAcceptCorrectSimpleRepFieldValue(idCalculatorMock);
		parser = newPositiveIntegerFieldParser(TYPE, FIELD, idCalculatorMock);
	}

	@Test
	public void shouldParseSimpleRepAndBuildSelector() {
		final Selector selector = parser.parse(TYPE, SIMPLE_REP);
		assertEquals(ID, selector.getId());
	}

	@Test
	public void shouldParseSimpleRepWithSpacesAndBuildSelector() {
		final Selector selector = parser.parse(TYPE, SIMPLE_REP_WITH_SPACES);
		assertEquals(ID, selector.getId());
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		final String asString = parser.toString();
		assertTrue(asString.contains(TYPE));
		assertTrue(asString.contains(FIELD));
	}

	private static final String TYPE = "TYPE";
	private static final String FIELD = "FIELD";
	private static final String UID = "AA";
	private static final String SIMPLE_REP = "1234";
	private static final String SIMPLE_REP_WITH_SPACES = "1234";
	private static final DataTypeId ID = new DataTypeId(TYPE, UID);

	private void onlyAcceptCorrectSimpleRepFieldValue(final IdCalculator idCalculatorMock) {
		when(idCalculatorMock.calculate(eq(TYPE), argThat(matchesField(FIELD, SIMPLE_REP)))).thenReturn(ID);
	}
}
