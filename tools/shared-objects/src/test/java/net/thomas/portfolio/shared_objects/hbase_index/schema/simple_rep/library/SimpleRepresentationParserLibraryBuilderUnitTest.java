package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.library;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.library.SimpleRepresentationParserLibraryBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.library.SimpleRepresentationParserLibrarySerializable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.parsers.DomainSimpleRepParser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.parsers.EmailAddressSimpleRepParser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.parsers.PositiveIntegerFieldSimpleRepParser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.parsers.SimpleRepresentationParserImpl;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.parsers.StringFieldSimpleRepParser;

public class SimpleRepresentationParserLibraryBuilderUnitTest {
	private SimpleRepresentationParserLibraryBuilder builder;
	private SimpleRepresentationParserImpl parser;

	@Before
	public void setUpForTest() {
		builder = new SimpleRepresentationParserLibraryBuilder();
		builder.setDataTypeFields(singletonMap(SOME_TYPE, new Fields()));
		parser = mock(SimpleRepresentationParserImpl.class);
		when(parser.getType()).thenReturn(SOME_TYPE);
	}

	@Test(expected = RuntimeException.class)
	public void shouldRejectAddingSameParserTwice() {
		builder.add(parser);
		builder.add(parser);
	}

	@Test
	public void shouldAddStringFieldParser() {
		builder.addStringFieldParser(SOME_TYPE, SOME_FIELD);
		final SimpleRepresentationParserImpl parser = buildAndGet(builder, SOME_TYPE);
		assertTrue(parser instanceof StringFieldSimpleRepParser);
	}

	@Test
	public void shouldAddPositiveIntegerParser() {
		builder.addIntegerFieldParser(SOME_TYPE, SOME_FIELD);
		final SimpleRepresentationParserImpl parser = buildAndGet(builder, SOME_TYPE);
		assertTrue(parser instanceof PositiveIntegerFieldSimpleRepParser);
	}

	@Test
	public void shouldAddDomainParser() {
		builder.addDomainParser();
		final SimpleRepresentationParserImpl parser = buildAndGet(builder, "Domain");
		assertTrue(parser instanceof DomainSimpleRepParser);
	}

	@Test
	public void shouldAddEmailAddressParser() {
		builder.addEmailAddressParser();
		final SimpleRepresentationParserImpl parser = buildAndGet(builder, "EmailAddress");
		assertTrue(parser instanceof EmailAddressSimpleRepParser);
	}

	private SimpleRepresentationParserImpl buildAndGet(SimpleRepresentationParserLibraryBuilder builder, String parser) {
		final SimpleRepresentationParserLibrarySerializable library = builder.build();
		return library.getParsers()
			.get(parser);
	}

	private static final String SOME_TYPE = "TYPE";
	private static final String SOME_FIELD = "some field";
}