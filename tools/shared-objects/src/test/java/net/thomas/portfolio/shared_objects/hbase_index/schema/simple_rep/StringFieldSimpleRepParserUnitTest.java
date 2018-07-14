package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep;

import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParser.newStringFieldParser;
import static net.thomas.portfolio.shared_objects.test_utils.DataTypeFieldMatcher.matchesField;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;

public class StringFieldSimpleRepParserUnitTest {

	private StringFieldSimpleRepParser parser;

	@Before
	public void setupForTest() {
		final IdCalculator idCalculatorMock = mock(IdCalculator.class);
		when(idCalculatorMock.calculate(eq(TYPE), argThat(matchesField(FIELD, SIMPLE_REP)))).thenReturn(ID);
		parser = newStringFieldParser(TYPE, FIELD, idCalculatorMock);
	}

	@Test
	public void shouldParseSimpleRepAndBuildSelector() {
		final Selector selector = parser.parse(TYPE, SIMPLE_REP);
		assertEquals(ID, selector.getId());
	}

	private static final String TYPE = "TYPE";
	private static final String FIELD = "FIELD";
	private static final String UID = "AA";
	private static final String SIMPLE_REP = "ABCD";
	private static final DataTypeId ID = new DataTypeId(TYPE, UID);
}