package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;

public class SimpleRepresentationParserUnitTest {

	private IdCalculator idCalculatorMock;

	@Before
	public void setUpForTest() {
		idCalculatorMock = mock(IdCalculator.class);
		when(idCalculatorMock.calculate(eq(TYPE), any())).thenReturn(ID);
	}

	@Test
	public void shouldHaveCorrectType() {
		final TestSimpleRepresentationParser parser = new TestSimpleRepresentationParser(TYPE, ANY_PATTERN, idCalculatorMock);
		final String type = parser.getType();
		assertEquals(TYPE, type);
	}

	@Test
	public void shouldSetLibraryAsFieldInParser() {
		final TestSimpleRepresentationParser parser = new TestSimpleRepresentationParser(TYPE, ANY_PATTERN, idCalculatorMock);
		final SimpleRepresentationParserLibrary libraryMock = mock(SimpleRepresentationParserLibrary.class);
		parser.setLibrary(libraryMock);
		assertSame(libraryMock, parser.getLibrary());
	}

	@Test
	public void shouldAcceptValueWithMatchingPattern() {
		final TestSimpleRepresentationParser parser = new TestSimpleRepresentationParser(TYPE, MATCHING_PATTERN, idCalculatorMock);
		assertTrue(parser.hasValidFormat(VALUE));
	}

	@Test
	public void shouldRejectValueWithMismatchingPattern() {
		final TestSimpleRepresentationParser parser = new TestSimpleRepresentationParser(TYPE, MISMATCHING_PATTERN, idCalculatorMock);
		assertFalse(parser.hasValidFormat(VALUE));
	}

	@Test
	public void shouldAddIdToEntity() {
		final TestSimpleRepresentationParser parser = new TestSimpleRepresentationParser(TYPE, ANY_PATTERN, idCalculatorMock);
		final Selector entity = parser.parse(TYPE, VALUE);
		assertSame(ID, entity.getId());
	}

	@Test
	public void shouldAddValueToFieldInEntity() {
		final TestSimpleRepresentationParser parser = new TestSimpleRepresentationParser(TYPE, ANY_PATTERN, idCalculatorMock);
		final Selector entity = parser.parse(TYPE, VALUE);
		assertEquals(VALUE, entity.get(FIELD));
	}

	private static final String ANY_PATTERN = ".+$";
	private static final String MATCHING_PATTERN = ANY_PATTERN;
	private static final String MISMATCHING_PATTERN = "$";
	private static final String FIELD = "FIELD";
	private static final String VALUE = "VALUE";
	private static final String TYPE = "TYPE";
	private static final DataTypeId ID = new DataTypeId(TYPE, "ABCD");

	private static class TestSimpleRepresentationParser extends SimpleRepresentationParser {

		public TestSimpleRepresentationParser(String type, String pattern, IdCalculator idCalculator) {
			super(type, pattern, idCalculator);
		}

		@Override
		public SimpleRepresentationParserLibrary getLibrary() {
			return library;
		}

		@Override
		protected void populateValues(DataType entity, String source) {
			entity.put(FIELD, VALUE);
		}

		@Override
		public String getImplementationClass() {
			return getClass().getSimpleName();
		}
	}
}
