package net.thomas.portfolio.hbase_index.schema.simple_rep.library;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.SimpleRepresentationParserImpl;
import net.thomas.portfolio.hbase_index.schema.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationParserLibraryImplUnitTest {
	private SimpleRepresentationParserLibraryBuilder libraryBuilder;
	private SimpleRepresentationParserImpl parser;

	@Before
	public void setUpForTest() {
		parser = mock(SimpleRepresentationParserImpl.class);
		when(parser.getType()).thenReturn(SOME_TYPE);
		libraryBuilder = new SimpleRepresentationParserLibraryBuilder().add(parser);
		when(parser.getType()).thenReturn(SOME_TYPE);
		when(parser.parse(eq(SOME_TYPE), eq(SOME_SIMPLE_REPRESENTATION))).thenReturn(SOME_SELECTOR);
	}

	@Test
	public void shouldAcceptFormatWhenMatchingParserExists() {
		when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(true);
		assertTrue(libraryBuilder.build().hasValidFormat(SOME_SIMPLE_REPRESENTATION));
	}

	@Test
	public void shouldRejectFormatWhenNoMatchingParserExists() {
		when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(false);
		assertFalse(libraryBuilder.build().hasValidFormat(SOME_SIMPLE_REPRESENTATION));
	}

	@Test
	public void shouldUseParserWhenParserForTypeExists() {
		when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(true);
		assertSame(SOME_SELECTOR, libraryBuilder.build().parse(SOME_TYPE, SOME_SIMPLE_REPRESENTATION));
	}

	@Test
	public void shouldReturnNullWhenFormatIsInvalidForParsers() {
		when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(false);
		assertNull(libraryBuilder.build().parse(SOME_TYPE, SOME_SIMPLE_REPRESENTATION));
	}

	@Test
	public void shouldReturnNullWhenNoParserForTypeExists() {
		when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(true);
		assertNull(libraryBuilder.build().parse(SOME_OTHER_TYPE, SOME_SIMPLE_REPRESENTATION));
	}

	//
	// @Test
	// public void shouldAddStringFieldParser() {
	// when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(true);
	// libraryBuilder.build().setSelectorTypes(singleton(SOME_TYPE));
	// final Selector selector =
	// first(libraryBuilder.getSelectorSuggestions(SOME_SIMPLE_REPRESENTATION));
	// assertEquals(SOME_SELECTOR, selector);
	// }
	//
	// @Test
	// public void shouldAddPositiveIntegerFieldParser() {
	// schemaBuilder.addSimpleRepresentationParser(SOME_SELECTOR_TYPE, SOME_FIELD,
	// PositiveIntegerFieldSimpleRepParser.class);
	// final HbaseIndexSchema schema = schemaBuilder.build();
	// final DataTypeId id = first(schema.getSelectorSuggestions(SOME_NUMBER));
	// assertEquals(SOME_SELECTOR_TYPE, id.type);
	// }
	//
	// @Test
	// public void shouldAddDomainParser() {
	// schemaBuilder.addSimpleRepresentationParser(SOME_TYPE, SOME_FIELD, DomainSimpleRepParser.class);
	// final HbaseIndexSchemaImpl schema = (HbaseIndexSchemaImpl) schemaBuilder.build();
	// final SimpleRepresentationParserImpl parser =
	// first(schema.getSimpleRepParsers().getParsers().values());
	// assertTrue(parser instanceof DomainSimpleRepParser);
	// }
	//
	// @Test
	// public void shouldAddEmailAddressParser() {
	// schemaBuilder.addSimpleRepresentationParser(SOME_TYPE, SOME_FIELD,
	// EmailAddressSimpleRepParser.class);
	// final HbaseIndexSchemaImpl schema = (HbaseIndexSchemaImpl) schemaBuilder.build();
	// final SimpleRepresentationParserImpl parser =
	// first(schema.getSimpleRepParsers().getParsers().values());
	// assertTrue(parser instanceof EmailAddressSimpleRepParser);
	// }
	//
	// @Test(expected = UnknownParserException.class)
	// public void shouldThrowExceptionWhenAddingUnknownParser() {
	// schemaBuilder.addSimpleRepresentationParser(SOME_TYPE, SOME_FIELD, UnknownParser.class);
	// }
	//
	private <T> T first(Iterable<T> values) {
		return values.iterator().next();
	}

	private boolean contains(Collection<String> container, String type) {
		return container.contains(type);
	}

	private static class UnknownParser extends SimpleRepresentationParserImpl {
		public UnknownParser(String type, String pattern, IdCalculator idCalculator) {
			super(SOME_TYPE, "", null);
		}

		@Override
		protected void populateValues(DataType entity, String source) {
		}
	}

	private static final String SOME_TYPE = "TYPE";
	private static final String SOME_OTHER_TYPE = "SomeOtherType";
	private static final String SOME_SELECTOR_TYPE = "SomeSelectorType";
	private static final String SOME_OTHER_SELECTOR_TYPE = "SomeOtherSelectorType";
	private static final String SOME_DOCUMENT_TYPE = "SomeDocumentType";
	private static final String SOME_OTHER_DOCUMENT_TYPE = "SomeOtherDocumentType";
	private static final String SOME_PATH = "SomePath";
	private static final String SOME_OTHER_PATH = "SomeOtherPath";
	private static final String SOME_FIELD = "SomeField";
	private static final Fields SOME_FIELDS = new Fields();
	private static final String SOME_SIMPLE_REPRESENTATION = "ab";
	private static final String SOME_NUMBER = "1234";
	private static final int TWO_SELECTOR_TYPES = 2;
	private static final int ONE_DOCUMENT_TYPE = 1;
	private static final DataTypeId SOME_ID = new DataTypeId(SOME_TYPE, "AA00");
	private static final Selector SOME_SELECTOR = new Selector(SOME_ID);
}