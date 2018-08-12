package net.thomas.portfolio.shared_objects.hbase_index.schema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.DomainSimpleRepParser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.EmailAddressSimpleRepParser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.PositiveIntegerFieldSimpleRepParser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParser;

public class HbaseIndexSchemaAndSchemaBuilderUnitTest {
	private HbaseIndexSchemaBuilder schemaBuilder;

	@Before
	public void setUpForTest() {
		schemaBuilder = new HbaseIndexSchemaBuilder();
		schemaBuilder.addDocumentTypes(SOME_DOCUMENT_TYPE, SOME_OTHER_DOCUMENT_TYPE);
		schemaBuilder.addSelectorTypes(SOME_SELECTOR_TYPE, SOME_OTHER_SELECTOR_TYPE);
	}

	@Test
	public void shouldContainFieldsAfterAddition() {
		schemaBuilder.addFields(SOME_TYPE, SOME_FIELDS);
		final HbaseIndexSchema schema = schemaBuilder.build();
		assertSame(SOME_FIELDS, schema.getFieldsForDataType(SOME_TYPE));
	}

	@Test
	public void shouldContainAllDataTypesThatHaveFields() {
		schemaBuilder.addFields(SOME_SELECTOR_TYPE, SOME_FIELDS);
		schemaBuilder.addFields(SOME_DOCUMENT_TYPE, SOME_FIELDS);
		final HbaseIndexSchema schema = schemaBuilder.build();
		assertTrue(contains(schema.getDataTypes(), SOME_DOCUMENT_TYPE));
		assertTrue(contains(schema.getDataTypes(), SOME_SELECTOR_TYPE));
	}

	@Test
	public void shouldContainAllDataTypeFields() {
		schemaBuilder.addFields(SOME_SELECTOR_TYPE, SOME_FIELDS);
		schemaBuilder.addFields(SOME_DOCUMENT_TYPE, SOME_FIELDS);
		final HbaseIndexSchemaImpl schema = (HbaseIndexSchemaImpl) schemaBuilder.build();
		final Map<String, Fields> dataTypeFields = schema.getDataTypeFields();
		assertSame(SOME_FIELDS, dataTypeFields.get(SOME_SELECTOR_TYPE));
		assertSame(SOME_FIELDS, dataTypeFields.get(SOME_DOCUMENT_TYPE));
	}

	@Test
	public void shouldContainDocumentTypeAfterAddition() {
		schemaBuilder.addDocumentTypes(SOME_TYPE);
		final HbaseIndexSchema schema = schemaBuilder.build();
		assertTrue(contains(schema.getDocumentTypes(), SOME_TYPE));
	}

	@Test
	public void shouldContainSelectorTypeAfterAddition() {
		schemaBuilder.addSelectorTypes(SOME_TYPE);
		final HbaseIndexSchema schema = schemaBuilder.build();
		assertTrue(contains(schema.getSelectorTypes(), SOME_TYPE));
	}

	@Test
	public void shouldContainSimpleRepresentableTypeAfterAddition() {
		schemaBuilder.addSimpleRepresentableTypes(SOME_TYPE);
		final HbaseIndexSchema schema = schemaBuilder.build();
		assertTrue(contains(schema.getSimpleRepresentableTypes(), SOME_TYPE));
	}

	@Test
	public void shouldContainIndexableAfterAddition() {
		schemaBuilder.addIndexable(SOME_TYPE, SOME_PATH, SOME_OTHER_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Collection<Indexable> indexables = schema.getIndexables(SOME_TYPE);
		assertEquals(1, indexables.size());
	}

	@Test
	public void shouldContainMultipleIndexablesBySelectorTypeAfterAddition() {
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_OTHER_DOCUMENT_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Collection<Indexable> indexables = schema.getIndexables(SOME_SELECTOR_TYPE);
		assertEquals(2, indexables.size());
	}

	@Test
	public void shouldContainMultipleIndexablesByDocumentTypeAfterAddition() {
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		schemaBuilder.addIndexable(SOME_OTHER_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Collection<Indexable> indexables = schema.getIndexables(SOME_DOCUMENT_TYPE);
		assertEquals(2, indexables.size());
	}

	@Test
	public void shouldContainMultipleIndexablesByPathAfterAddition() {
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_OTHER_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Collection<Indexable> indexables = schema.getIndexables(SOME_DOCUMENT_TYPE);
		assertEquals(2, indexables.size());
	}

	@Test
	public void shouldContainMultipleIndexableDocumentTypesAfterAddition() {
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_OTHER_DOCUMENT_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Collection<String> documentTypes = schema.getIndexableDocumentTypes(SOME_SELECTOR_TYPE);
		assertTrue(documentTypes.contains(SOME_DOCUMENT_TYPE));
		assertTrue(documentTypes.contains(SOME_OTHER_DOCUMENT_TYPE));
	}

	@Test
	public void shouldContainMultipleIndexablePathsAfterAddition() {
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_OTHER_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Collection<String> paths = schema.getIndexableRelations(SOME_SELECTOR_TYPE);
		assertTrue(paths.contains(SOME_PATH));
		assertTrue(paths.contains(SOME_OTHER_PATH));
	}

	@Test
	public void shouldReturnAllIndexables() {
		schemaBuilder.addIndexable(SOME_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		schemaBuilder.addIndexable(SOME_OTHER_SELECTOR_TYPE, SOME_PATH, SOME_DOCUMENT_TYPE, SOME_FIELD);
		final HbaseIndexSchemaImpl schema = (HbaseIndexSchemaImpl) schemaBuilder.build();
		final Map<String, Collection<Indexable>> indexables = schema.getIndexables();
		assertEquals(TWO_SELECTOR_TYPES + ONE_DOCUMENT_TYPE, indexables.size());
	}

	@Test
	public void shouldContainSelectorTypeInIndexable() {
		schemaBuilder.addIndexable(SOME_TYPE, SOME_PATH, SOME_OTHER_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Indexable indexable = first(schema.getIndexables(SOME_TYPE));
		assertEquals(SOME_TYPE, indexable.selectorType);
	}

	@Test
	public void shouldContainPathInIndexable() {
		schemaBuilder.addIndexable(SOME_TYPE, SOME_PATH, SOME_OTHER_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Indexable indexable = first(schema.getIndexables(SOME_TYPE));
		assertEquals(SOME_PATH, indexable.path);
	}

	@Test
	public void shouldContainDocumentTypeInIndexable() {
		schemaBuilder.addIndexable(SOME_TYPE, SOME_PATH, SOME_OTHER_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Indexable indexable = first(schema.getIndexables(SOME_TYPE));
		assertEquals(SOME_OTHER_TYPE, indexable.documentType);
	}

	@Test
	public void shouldContainFieldNameInIndexable() {
		schemaBuilder.addIndexable(SOME_TYPE, SOME_PATH, SOME_OTHER_TYPE, SOME_FIELD);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final Indexable indexable = first(schema.getIndexables(SOME_TYPE));
		assertEquals(SOME_FIELD, indexable.documentField);
	}

	@Test
	public void shouldAddStringFieldParser() {
		schemaBuilder.addFields(SOME_SELECTOR_TYPE, SOME_FIELDS);
		schemaBuilder.addSimpleRepresentationParser(SOME_SELECTOR_TYPE, SOME_FIELD, StringFieldSimpleRepParser.class);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final DataTypeId id = first(schema.getSelectorSuggestions(SOME_SIMPLE_REPRESENTATION));
		assertEquals(SOME_SELECTOR_TYPE, id.type);
	}

	@Test
	public void shouldAddPositiveIntegerFieldParser() {
		schemaBuilder.addFields(SOME_SELECTOR_TYPE, SOME_FIELDS);
		schemaBuilder.addSimpleRepresentationParser(SOME_SELECTOR_TYPE, SOME_FIELD, PositiveIntegerFieldSimpleRepParser.class);
		final HbaseIndexSchema schema = schemaBuilder.build();
		final DataTypeId id = first(schema.getSelectorSuggestions(SOME_NUMBER));
		assertEquals(SOME_SELECTOR_TYPE, id.type);
	}

	@Test
	public void shouldAddDomainParser() {
		schemaBuilder.addSimpleRepresentationParser(SOME_TYPE, SOME_FIELD, DomainSimpleRepParser.class);
		final HbaseIndexSchemaImpl schema = (HbaseIndexSchemaImpl) schemaBuilder.build();
		final SimpleRepresentationParser parser = first(schema.getSimpleRepParsers()
			.getParsers()
			.values());
		assertTrue(parser instanceof DomainSimpleRepParser);
	}

	@Test
	public void shouldAddEmailAddressParser() {
		schemaBuilder.addSimpleRepresentationParser(SOME_TYPE, SOME_FIELD, EmailAddressSimpleRepParser.class);
		final HbaseIndexSchemaImpl schema = (HbaseIndexSchemaImpl) schemaBuilder.build();
		final SimpleRepresentationParser parser = first(schema.getSimpleRepParsers()
			.getParsers()
			.values());
		assertTrue(parser instanceof EmailAddressSimpleRepParser);
	}

	@Test(expected = UnknownParserException.class)
	public void shouldThrowExceptionWhenAddingUnknownParser() {
		schemaBuilder.addSimpleRepresentationParser(SOME_TYPE, SOME_FIELD, UnknownParser.class);
	}

	private <T> T first(Iterable<T> values) {
		return values.iterator()
			.next();
	}

	private boolean contains(Collection<String> container, String type) {
		return container.contains(type);
	}

	private static class UnknownParser extends SimpleRepresentationParser {
		public UnknownParser(String type, String pattern, IdCalculator idCalculator) {
			super(SOME_TYPE, "", null);
		}

		@Override
		protected void populateValues(DataType entity, String source) {
		}

		@Override
		public String getImplementationClass() {
			return getClass().getSimpleName();
		}
	}

	private static final String SOME_TYPE = "SomeType";
	private static final String SOME_OTHER_TYPE = "SomeOtherType";
	private static final String SOME_SELECTOR_TYPE = "SomeSelectorType";
	private static final String SOME_OTHER_SELECTOR_TYPE = "SomeOtherSelectorType";
	private static final String SOME_DOCUMENT_TYPE = "SomeDocumentType";
	private static final String SOME_OTHER_DOCUMENT_TYPE = "SomeOtherDocumentType";
	private static final String SOME_PATH = "SomePath";
	private static final String SOME_OTHER_PATH = "SomeOtherPath";
	private static final String SOME_FIELD = "SomeField";
	private static final Fields SOME_FIELDS = new Fields();
	private static final String SOME_SIMPLE_REPRESENTATION = "someSimpleRepresentation";
	private static final String SOME_NUMBER = "1234";
	private static final int TWO_SELECTOR_TYPES = 2;
	private static final int ONE_DOCUMENT_TYPE = 1;
}