package net.thomas.portfolio.hbase_index.schema;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.createNonKeyArrayField;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.nonKeyString;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.strings;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.timestamp;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.STRING;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.UidConverter;

public class IdCalculatorUnitTest {
	private IdCalculator simpleTypeIdGenerator;
	private IdCalculator simpleTypeWithTimestampIdGenerator;
	private IdCalculator simpleTypeWithArrayIdGenerator;
	private IdCalculator nullReferenceTypeIdGenerator;
	private IdCalculator recursiveTypeIdGenerator;
	private IdCalculator complexTypeIdGenerator;
	private IdCalculator uniqueKeyIdGenerator;
	private IdCalculator documentIdGenerator;

	@Before
	public void setupDataTypes() {
		simpleTypeIdGenerator = new IdCalculator(SIMPLE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		simpleTypeWithTimestampIdGenerator = new IdCalculator(SIMPLE_TYPE_WITH_TIMESTAMP_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		simpleTypeWithArrayIdGenerator = new IdCalculator(SIMPLE_TYPE_WITH_ARRAY_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		nullReferenceTypeIdGenerator = new IdCalculator(NULL_REFERENCE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		recursiveTypeIdGenerator = new IdCalculator(RECURSIVE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		complexTypeIdGenerator = new IdCalculator(COMPLEX_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		uniqueKeyIdGenerator = new IdCalculator(SIMPLE_TYPE_FIELDS, KEYS_SHOULD_BE_UNIQUE);
		documentIdGenerator = new IdCalculator(DOCUMENT_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
	}

	@Test
	public void shouldHaveCorrectTypeInId() {
		final DataTypeId actualId = simpleTypeIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		assertEquals(SIMPLE_TYPE, actualId.type);
	}

	@Test
	public void shouldIncludeFieldInCalculation() {
		final String expectedUid = SIMPLE_ENTITY.getId().uid;
		final DataTypeId actualId = simpleTypeIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldIncludeTimestampFieldInCalculation() {
		final String expectedUid = SIMPLE_ENTITY_WITH_TIMESTAMP.getId().uid;
		final DataTypeId actualId = simpleTypeWithTimestampIdGenerator.calculate(SIMPLE_TYPE_WITH_TIMESTAMP, SIMPLE_ENTITY_WITH_TIMESTAMP);
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldIncludeArrayFieldInCalculation() {
		final String expectedUid = SIMPLE_ENTITY_WITH_ARRAY.getId().uid;
		final DataTypeId actualId = simpleTypeWithArrayIdGenerator.calculate(SIMPLE_TYPE_WITH_ARRAY, SIMPLE_ENTITY_WITH_ARRAY);
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldSurviveNullFieldInCalculation() {
		final Selector selector = new Selector();
		final DataTypeId actualId = simpleTypeIdGenerator.calculate(SIMPLE_TYPE, selector);
		assertNotNull(actualId.uid);
	}

	@Test
	public void shouldHandleNullReferenceCorrectly() {
		final String expectedUid = NULL_REFERENCE_ENTITY.getId().uid;
		final DataTypeId actualId = nullReferenceTypeIdGenerator.calculate(NULL_REFERENCE_TYPE, NULL_REFERENCE_ENTITY);
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldHandleRecursiveEntityCorrectly() {
		final String expectedUid = RECURSIVE_ENTITY.getId().uid;
		final DataTypeId actualId = recursiveTypeIdGenerator.calculate(RECURSIVE_TYPE, RECURSIVE_ENTITY);
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldIncludeSubTypeIdsInCalculation() {
		final String expectedUid = COMPLEX_ENTITY.getId().uid;
		final DataTypeId actualId = complexTypeIdGenerator.calculate(COMPLEX_TYPE, COMPLEX_ENTITY);
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldGenerateUniqueKeyEveryTime() {
		final DataTypeId actualId1 = uniqueKeyIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		final DataTypeId actualId2 = uniqueKeyIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		assertNotEquals(actualId1.uid, actualId2.uid);
	}

	@Test
	public void shouldHandleDocumentEntityCorrectly() {
		final String expectedUid = DOCUMENT_ENTITY.getId().uid;
		final DataTypeId actualId = documentIdGenerator.calculate(DOCUMENT_TYPE, DOCUMENT_ENTITY);
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldHaveValidHashCodeFunction() {
		assertEquals(simpleTypeIdGenerator.hashCode(), new IdCalculator(SIMPLE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT).hashCode());
		assertNotEquals(simpleTypeIdGenerator.hashCode(), complexTypeIdGenerator.hashCode());
		assertNotEquals(simpleTypeIdGenerator.hashCode(), recursiveTypeIdGenerator.hashCode());
		assertNotEquals(recursiveTypeIdGenerator.hashCode(), complexTypeIdGenerator.hashCode());
		assertNotEquals(simpleTypeIdGenerator.hashCode(), uniqueKeyIdGenerator.hashCode());
	}

	@Test
	public void shouldHaveValidEqualsFunction() {
		assertEquals(simpleTypeIdGenerator, simpleTypeIdGenerator);
		assertEquals(simpleTypeIdGenerator, new IdCalculator(SIMPLE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT));
		assertNotEquals(simpleTypeIdGenerator, null);
		assertNotEquals(simpleTypeIdGenerator, "");
		assertNotEquals(simpleTypeIdGenerator, complexTypeIdGenerator);
		assertNotEquals(simpleTypeIdGenerator, recursiveTypeIdGenerator);
		assertNotEquals(simpleTypeIdGenerator, uniqueKeyIdGenerator);
	}

	@Test
	public void shouldHaveValidToStringFunction() {
		assertToStringContainsAllFieldsFromObject(simpleTypeIdGenerator);
	}

	private static final String VALUE_FIELD = "value";
	private static final String NON_KEY_VALUE_FIELD = "nonKeyValue";
	private static final String REFERENCE_FIELD = "reference";
	private static final String RECURSIVE_SUB_TYPE_FIELD = "subType";
	private static final String COMPLEX_ENTITY_FIELD_1 = "complexEntityField";
	private static final String COMPLEX_ENTITY_FIELD_2 = "complexEntityField2";
	private static final String SIMPLE_TYPE = "SimpleType";
	private static final String SIMPLE_TYPE_WITH_TIMESTAMP = "SimpleTypeWithTimestamp";
	private static final String SIMPLE_TYPE_WITH_ARRAY = "SimpleTypeWithArray";
	private static final String NULL_REFERENCE_TYPE = "NullReferenceType";
	private static final String RECURSIVE_TYPE = "RecursiveType";
	private static final String COMPLEX_TYPE = "ComplexType";
	private static final String DOCUMENT_TYPE = "DocumentType";

	private static final String RECURSIVE_SUBTYPE_UID = "ABCD1234";
	private static final boolean KEYS_SHOULD_BE_CONSISTENT = false;
	private static final boolean KEYS_SHOULD_BE_UNIQUE = true;

	private static final Fields SIMPLE_TYPE_FIELDS;
	private static final Fields SIMPLE_TYPE_WITH_TIMESTAMP_FIELDS;
	private static final Fields SIMPLE_TYPE_WITH_ARRAY_FIELDS;
	private static final Fields NULL_REFERENCE_TYPE_FIELDS;
	private static final Fields RECURSIVE_TYPE_FIELDS;
	private static final Fields COMPLEX_TYPE_FIELDS;
	private static final Fields DOCUMENT_FIELDS;
	private static final Selector SIMPLE_ENTITY;
	private static final Selector SIMPLE_ENTITY_WITH_TIMESTAMP;
	private static final Selector SIMPLE_ENTITY_WITH_ARRAY;
	private static final Selector NULL_REFERENCE_ENTITY;
	private static final Selector RECURSIVE_ENTITY;
	private static final Selector COMPLEX_ENTITY;
	private static final Document DOCUMENT_ENTITY;

	static {
		SIMPLE_TYPE_FIELDS = fields(string(VALUE_FIELD), nonKeyString(NON_KEY_VALUE_FIELD));
		SIMPLE_TYPE_WITH_TIMESTAMP_FIELDS = fields(timestamp(VALUE_FIELD));
		SIMPLE_TYPE_WITH_ARRAY_FIELDS = fields(strings(VALUE_FIELD), createNonKeyArrayField(NON_KEY_VALUE_FIELD, STRING));
		NULL_REFERENCE_TYPE_FIELDS = fields(dataType(REFERENCE_FIELD, SIMPLE_TYPE));
		RECURSIVE_TYPE_FIELDS = fields(string(VALUE_FIELD), dataType(RECURSIVE_SUB_TYPE_FIELD, RECURSIVE_TYPE));
		COMPLEX_TYPE_FIELDS = fields(dataType(COMPLEX_ENTITY_FIELD_1, SIMPLE_TYPE), dataType(COMPLEX_ENTITY_FIELD_2, RECURSIVE_TYPE));
		DOCUMENT_FIELDS = fields(string(VALUE_FIELD));

		SIMPLE_ENTITY = setupSimpleEntity();
		SIMPLE_ENTITY_WITH_TIMESTAMP = setupSimpleEntityWithTimestamp();
		SIMPLE_ENTITY_WITH_ARRAY = setupSimpleEntityWithArray();
		NULL_REFERENCE_ENTITY = setupNullEntity();
		RECURSIVE_ENTITY = setupRecursiveEntity();
		COMPLEX_ENTITY = setupComplexEntity();
		DOCUMENT_ENTITY = setupDocumentEntity();
		loadCorrectEntityIdsIntoEntities();
	}

	private static Selector setupSimpleEntity() {
		final Selector entity = new Selector();
		entity.put(VALUE_FIELD, "simpleValue");
		return entity;
	}

	private static Selector setupSimpleEntityWithTimestamp() {
		final Selector entity = new Selector();
		entity.put(VALUE_FIELD, new Timestamp(1L));
		return entity;
	}

	private static Selector setupSimpleEntityWithArray() {
		final Selector entity = new Selector();
		entity.put(VALUE_FIELD, asList("simpleValue"));
		return entity;
	}

	private static Selector setupNullEntity() {
		return new Selector();
	}

	private static Selector setupRecursiveEntity() {
		final Selector entity = new Selector();
		entity.put(VALUE_FIELD, "recursiveEntityValue1");
		final Selector recursiveSubEntity = new Selector();
		recursiveSubEntity.put(VALUE_FIELD, "recursiveEntityValue2");
		recursiveSubEntity.setId(new DataTypeId(RECURSIVE_TYPE, RECURSIVE_SUBTYPE_UID));
		entity.put(RECURSIVE_SUB_TYPE_FIELD, recursiveSubEntity);
		return entity;
	}

	private static Selector setupComplexEntity() {
		final Selector entity = new Selector();
		entity.put(COMPLEX_ENTITY_FIELD_1, SIMPLE_ENTITY);
		entity.put(COMPLEX_ENTITY_FIELD_2, RECURSIVE_ENTITY);
		return entity;
	}

	private static Document setupDocumentEntity() {
		final Document entity = new Document();
		entity.put(VALUE_FIELD, "simpleValue");
		entity.setTimeOfEvent(new Timestamp(1000L));
		return entity;
	}

	private static void loadCorrectEntityIdsIntoEntities() {
		final UidConverter keyConverter = new UidConverter();
		SIMPLE_ENTITY.setId(new DataTypeId(SIMPLE_TYPE, hashSimpleEntity(keyConverter)));
		SIMPLE_ENTITY_WITH_TIMESTAMP.setId(new DataTypeId(SIMPLE_TYPE_WITH_TIMESTAMP, hashSimpleEntityWithTimestamp(keyConverter)));
		SIMPLE_ENTITY_WITH_ARRAY.setId(new DataTypeId(SIMPLE_TYPE_WITH_ARRAY, hashSimpleEntityWithArray(keyConverter)));
		NULL_REFERENCE_ENTITY.setId(new DataTypeId(NULL_REFERENCE_TYPE, hashNullReferenceEntity(keyConverter)));
		RECURSIVE_ENTITY.setId(new DataTypeId(RECURSIVE_TYPE, hashRecursiveEntity(keyConverter)));
		COMPLEX_ENTITY.setId(new DataTypeId(COMPLEX_TYPE, hashComplexEntity(keyConverter)));
		DOCUMENT_ENTITY.setId(new DataTypeId(DOCUMENT_TYPE, hashDocumentEntity(keyConverter)));
	}

	private static String hashSimpleEntity(UidConverter keyConverter) {
		final MessageDigest hasher = hasher();
		hasher.update(SIMPLE_TYPE.getBytes());
		final String value = (String) getField(SIMPLE_ENTITY, VALUE_FIELD);
		hasher.update(value.getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashSimpleEntityWithTimestamp(UidConverter keyConverter) {
		final MessageDigest hasher = hasher();
		hasher.update(SIMPLE_TYPE_WITH_TIMESTAMP.getBytes());
		final Timestamp value = (Timestamp) getField(SIMPLE_ENTITY_WITH_TIMESTAMP, VALUE_FIELD);
		hasher.update(valueOf(value.getTimestamp()).getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashSimpleEntityWithArray(UidConverter keyConverter) {
		final MessageDigest hasher = hasher();
		hasher.update(SIMPLE_TYPE_WITH_ARRAY.getBytes());
		@SuppressWarnings("unchecked")
		final List<String> values = (List<String>) getField(SIMPLE_ENTITY_WITH_ARRAY, VALUE_FIELD);
		for (final String value : values) {
			hasher.update(value.getBytes());
		}
		return keyConverter.convert(hasher.digest());
	}

	private static String hashNullReferenceEntity(UidConverter keyConverter) {
		final MessageDigest hasher = hasher();
		hasher.update(NULL_REFERENCE_TYPE.getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashRecursiveEntity(UidConverter keyConverter) {
		final MessageDigest hasher = hasher();
		hasher.update(RECURSIVE_TYPE.getBytes());
		final String value = (String) getField(RECURSIVE_ENTITY, VALUE_FIELD);
		hasher.update(value.getBytes());
		hasher.update(RECURSIVE_SUBTYPE_UID.getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashComplexEntity(UidConverter keyConverter) {
		final MessageDigest hasher = hasher();
		hasher.update(COMPLEX_TYPE.getBytes());
		hasher.update(hashSimpleEntity(keyConverter).getBytes());
		hasher.update(hashRecursiveEntity(keyConverter).getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashDocumentEntity(UidConverter keyConverter) {
		final MessageDigest hasher = hasher();
		hasher.update(DOCUMENT_TYPE.getBytes());
		final String value = (String) getField(DOCUMENT_ENTITY, VALUE_FIELD);
		hasher.update(value.getBytes());
		hasher.update(valueOf(DOCUMENT_ENTITY.getTimeOfEvent()
			.getTimestamp()).getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static MessageDigest hasher() {
		try {
			return MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 hasher is no longer available", e);
		}
	}

	private static <T> T getField(DataType entity, String field) {
		return entity.get(field);
	}
}
