package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationParserLibraryImplUnitTest {
	private SimpleRepresentationParserLibrary library;
	private SimpleRepresentationParser parser;

	@Before
	public void setUpForTest() {
		parser = mock(SimpleRepresentationParser.class);
		when(parser.getType()).thenReturn(SOME_TYPE);
		library = new SimpleRepresentationParserLibraryBuilder().setDataTypeFields(singletonMap(SOME_TYPE, new Fields()))
			.add(parser)
			.build();
		when(parser.getType()).thenReturn(SOME_TYPE);
		when(parser.parse(eq(SOME_TYPE), eq(SOME_STRING))).thenReturn(SOME_SELECTOR);
	}

	@Test
	public void shouldAcceptFormatWhenMatchingParserExists() {
		when(parser.hasValidFormat(SOME_STRING)).thenReturn(true);
		assertTrue(library.hasValidFormat(SOME_STRING));
	}

	@Test
	public void shouldRejectFormatWhenNoMatchingParserExists() {
		when(parser.hasValidFormat(SOME_STRING)).thenReturn(false);
		assertFalse(library.hasValidFormat(SOME_STRING));
	}

	@Test
	public void shouldUseParserWhenParserForTypeExists() {
		when(parser.hasValidFormat(SOME_STRING)).thenReturn(true);
		assertSame(SOME_SELECTOR, library.parse(SOME_TYPE, SOME_STRING));
	}

	@Test
	public void shouldReturnNullWhenFormatIsInvalidForParsers() {
		when(parser.hasValidFormat(SOME_STRING)).thenReturn(false);
		assertNull(library.parse(SOME_TYPE, SOME_STRING));
	}

	@Test
	public void shouldReturnNullWhenNoParserForTypeExists() {
		when(parser.hasValidFormat(SOME_STRING)).thenReturn(true);
		assertNull(library.parse(ANOTHER_TYPE, SOME_STRING));
	}

	private static final String SOME_TYPE = "TYPE";
	private static final String ANOTHER_TYPE = "ANOTHER TYPE";
	private static final String SOME_STRING = "ab";
	private static final Selector SOME_SELECTOR = new Selector();
}