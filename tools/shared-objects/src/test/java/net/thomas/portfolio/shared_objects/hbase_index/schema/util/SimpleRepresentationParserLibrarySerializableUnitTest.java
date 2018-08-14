package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.assertCanSerializeAndDeserialize;
import static net.thomas.portfolio.testing_tools.SerializationDeserializationUtil.serializeDeserialize;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;

public class SimpleRepresentationParserLibrarySerializableUnitTest {

	private static final String SOME_TYPE = "some type";
	private static final String SOME_FIELD = "some field";
	private SimpleRepresentationParserLibraryBuilder builder;
	private SimpleRepresentationParser parserMock;

	@Before
	public void setUpForTest() {
		builder = new SimpleRepresentationParserLibraryBuilder();
		builder.setDataTypeFields(singletonMap(SOME_TYPE, new Fields()));
		parserMock = mock(SimpleRepresentationParser.class);
		when(parserMock.getType()).thenReturn(TYPE);
	}

	@Test
	public void shouldSerializeDeserializeLibraryWithSimpleFieldSimpleRepParser() {
		builder.addStringFieldParser(SOME_TYPE, SOME_FIELD);
		assertCanSerializeAndDeserialize(builder.build());
	}

	@Test
	public void shouldAddPositiveIntegerParser() {
		builder.addIntegerFieldParser(SOME_TYPE, SOME_FIELD);
		assertCanSerializeAndDeserialize(builder.build());
	}

	@Test
	public void shouldAddDomainParser() {
		builder.addDomainParser();
		assertCanSerializeAndDeserialize(builder.build());
	}

	@Test
	public void shouldAddEmailAddressParser() {
		builder.addEmailAddressParser();
		assertCanSerializeAndDeserialize(builder.build());
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		final SimpleRepresentationParserLibrarySerializable library = builder.build();
		assertEquals(library.hashCode(), copy(library).hashCode());
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		final SimpleRepresentationParserLibrarySerializable library = builder.build();
		assertEquals(library, library);
		assertNotEquals(library, null);
		assertNotEquals(library, "");
		assertEquals(library, copy(library));
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(builder.build());
	}

	private SimpleRepresentationParserLibrary copy(SimpleRepresentationParserLibrary library) {
		return serializeDeserialize(library);
	}

	private static final String TYPE = "TYPE";
}
