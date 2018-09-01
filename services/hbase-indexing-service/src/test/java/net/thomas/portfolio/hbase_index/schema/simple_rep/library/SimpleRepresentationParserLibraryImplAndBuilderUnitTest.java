package net.thomas.portfolio.hbase_index.schema.simple_rep.library;

import static java.util.Collections.singleton;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.SimpleRepresentationParserImpl;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class SimpleRepresentationParserLibraryImplAndBuilderUnitTest {
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

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenAddingSameParserTwice() {
		libraryBuilder.add(parser);
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

	@Test
	public void shouldSuggestMatchingSelector() {
		when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(true);
		final SimpleRepresentationParserLibrary library = libraryBuilder.build();
		library.setSelectorTypes(singleton(SOME_TYPE));
		final Selector selector = first(library.getSelectorSuggestions(SOME_SIMPLE_REPRESENTATION));
		assertEquals(SOME_SELECTOR, selector);
	}

	@Test
	public void shouldIgnoreTypeWhenSelectorIsNotAMatch() {
		when(parser.hasValidFormat(SOME_SIMPLE_REPRESENTATION)).thenReturn(false);
		final SimpleRepresentationParserLibrary library = libraryBuilder.build();
		library.setSelectorTypes(singleton(SOME_TYPE));
		assertTrue(library.getSelectorSuggestions(SOME_SIMPLE_REPRESENTATION).isEmpty());
	}

	@Test
	public void shouldAddStringFieldParser() {
		libraryBuilder.addFields(SOME_OTHER_TYPE, fields(string(SOME_FIELD))).addStringFieldParser(SOME_OTHER_TYPE, SOME_FIELD);
		final Selector selector = libraryBuilder.build().parse(SOME_OTHER_TYPE, SOME_SIMPLE_REPRESENTATION);
		assertEquals(SOME_OTHER_TYPE, selector.getId().type);
	}

	@Test
	public void shouldAddPositiveIntegerFieldParser() {
		libraryBuilder.addFields(SOME_OTHER_TYPE, fields(string(SOME_FIELD))).addPositiveIntegerFieldParser(SOME_OTHER_TYPE, SOME_FIELD);
		final Selector selector = libraryBuilder.build().parse(SOME_OTHER_TYPE, SOME_POSITIVE_NUMBER);
		assertEquals(SOME_OTHER_TYPE, selector.getId().type);
	}

	@Test
	public void shouldAddDomainParser() {
		libraryBuilder.addFields(DOMAIN, fields(string("domainPart"), dataType("domain", DOMAIN))).addDomainParser();
		final Selector selector = libraryBuilder.build().parse(DOMAIN, SOME_DOMAIN);
		assertEquals(DOMAIN, selector.getId().type);
	}

	@Test
	public void shouldAddEmailAddressParser() {
		libraryBuilder.addFields(EMAIL_ADDRESS, fields(dataType("localname", LOCALNAME)));
		libraryBuilder.addFields(LOCALNAME, fields(string("name")));
		libraryBuilder.addEmailAddressParser();
		final Selector selector = libraryBuilder.build().parse(EMAIL_ADDRESS, SOME_EMAIL_ADDRESS);
		assertEquals(EMAIL_ADDRESS, selector.getId().type);
	}

	private <T> T first(Iterable<T> values) {
		return values.iterator().next();
	}

	private static final String SOME_TYPE = "TYPE";
	private static final String SOME_OTHER_TYPE = "SomeOtherType";
	private static final String SOME_FIELD = "SomeField";
	private static final String SOME_SIMPLE_REPRESENTATION = "ab";
	private static final String SOME_POSITIVE_NUMBER = "1234";
	private static final String LOCALNAME = Localname.class.getSimpleName();
	private static final String DOMAIN = Domain.class.getSimpleName();
	private static final String SOME_DOMAIN = "abc.de";
	private static final String EMAIL_ADDRESS = EmailAddress.class.getSimpleName();
	private static final String SOME_EMAIL_ADDRESS = "abcd@abc.de";
	private static final DataTypeId SOME_ID = new DataTypeId(SOME_TYPE, "AA00");
	private static final Selector SOME_SELECTOR = new Selector(SOME_ID);
}