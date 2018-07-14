package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.UidConverter;

public class IdCalculatorUnitTest {
	private IdCalculator simpleTypeIdGenerator;
	private IdCalculator nullReferenceTypeIdGenerator;
	private IdCalculator recursiveTypeIdGenerator;
	private IdCalculator complexTypeIdGenerator;
	private IdCalculator uniqueKeyIdGenerator;

	@Before
	public void setupDataTypes() {
		simpleTypeIdGenerator = new IdCalculator(SIMPLE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		nullReferenceTypeIdGenerator = new IdCalculator(NULL_REFERENCE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		recursiveTypeIdGenerator = new IdCalculator(RECURSIVE_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		complexTypeIdGenerator = new IdCalculator(COMPLEX_TYPE_FIELDS, KEYS_SHOULD_BE_CONSISTENT);
		uniqueKeyIdGenerator = new IdCalculator(SIMPLE_TYPE_FIELDS, KEYS_SHOULD_BE_UNIQUE);
	}

	@Test
	public void shouldHaveCorrectTypeInId() throws NoSuchAlgorithmException {
		final DataTypeId actualId = simpleTypeIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		assertEquals(SIMPLE_TYPE, actualId.type);
	}

	@Test
	public void shouldIncludeFieldInCalculation() throws NoSuchAlgorithmException {
		final DataTypeId actualId = simpleTypeIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		final String expectedUid = SIMPLE_ENTITY.getId().uid;
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldHandleNullReferenceCorrectly() throws NoSuchAlgorithmException {
		final DataTypeId actualId = nullReferenceTypeIdGenerator.calculate(NULL_REFERENCE_TYPE, NULL_REFERENCE_ENTITY);
		final String expectedUid = NULL_REFERENCE_ENTITY.getId().uid;
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldHandleRecursiveEntityCorrectly() throws NoSuchAlgorithmException {
		final DataTypeId actualId = recursiveTypeIdGenerator.calculate(RECURSIVE_TYPE, RECURSIVE_ENTITY);
		final String expectedUid = RECURSIVE_ENTITY.getId().uid;
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldIncludeSubTypeIdsInCalculation() throws NoSuchAlgorithmException {
		final DataTypeId actualId = complexTypeIdGenerator.calculate(COMPLEX_TYPE, COMPLEX_ENTITY);
		final String expectedUid = COMPLEX_ENTITY.getId().uid;
		assertEquals(expectedUid, actualId.uid);
	}

	@Test
	public void shouldIncludeGenerateUniqueKeyEveryTime() throws NoSuchAlgorithmException {
		final DataTypeId actualId1 = uniqueKeyIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		final DataTypeId actualId2 = uniqueKeyIdGenerator.calculate(SIMPLE_TYPE, SIMPLE_ENTITY);
		assertNotEquals(actualId1.uid, actualId2.uid);
	}

	private static final String VALUE_FIELD = "value";
	private static final String REFERENCE_FIELD = "reference";
	private static final String RECURSIVE_SUB_TYPE_FIELD = "subType";
	private static final String COMPLEX_ENTITY_FIELD_1 = "complexEntityField";
	private static final String COMPLEX_ENTITY_FIELD_2 = "complexEntityField2";
	private static final String SIMPLE_TYPE = "SimpleType";
	private static final String NULL_REFERENCE_TYPE = "NullReferenceType";
	private static final String RECURSIVE_TYPE = "RecursiveType";
	private static final String COMPLEX_TYPE = "ComplexType";

	private static final String RECURSIVE_SUBTYPE_UID = "ABCD1234";
	private static final boolean KEYS_SHOULD_BE_CONSISTENT = false;
	private static final boolean KEYS_SHOULD_BE_UNIQUE = true;

	private static final Fields SIMPLE_TYPE_FIELDS;
	private static final Fields NULL_REFERENCE_TYPE_FIELDS;
	private static final Fields RECURSIVE_TYPE_FIELDS;
	private static final Fields COMPLEX_TYPE_FIELDS;
	private static final Selector SIMPLE_ENTITY;
	private static final Selector NULL_REFERENCE_ENTITY;
	private static final Selector RECURSIVE_ENTITY;
	private static final Selector COMPLEX_ENTITY;

	static {
		SIMPLE_TYPE_FIELDS = fields(string(VALUE_FIELD));
		NULL_REFERENCE_TYPE_FIELDS = fields(dataType(REFERENCE_FIELD, SIMPLE_TYPE));
		RECURSIVE_TYPE_FIELDS = fields(string(VALUE_FIELD), dataType(RECURSIVE_SUB_TYPE_FIELD, RECURSIVE_TYPE));
		COMPLEX_TYPE_FIELDS = fields(dataType(COMPLEX_ENTITY_FIELD_1, SIMPLE_TYPE), dataType(COMPLEX_ENTITY_FIELD_2, RECURSIVE_TYPE));

		SIMPLE_ENTITY = setupSimpleEntity();
		NULL_REFERENCE_ENTITY = setupNullEntity();
		RECURSIVE_ENTITY = setupRecursiveEntity();
		COMPLEX_ENTITY = setupComplexEntity();
		loadCorrectEntityIdsIntoEntities();
	}

	private static Selector setupSimpleEntity() {
		final Selector entity = new Selector();
		entity.put(VALUE_FIELD, "simpleValue");
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

	private static void loadCorrectEntityIdsIntoEntities() {
		try {
			final UidConverter keyConverter = new UidConverter();
			SIMPLE_ENTITY.setId(new DataTypeId(SIMPLE_TYPE, hashSimpleEntity(keyConverter)));
			NULL_REFERENCE_ENTITY.setId(new DataTypeId(NULL_REFERENCE_TYPE, hashNullReferenceEntity(keyConverter)));
			RECURSIVE_ENTITY.setId(new DataTypeId(RECURSIVE_TYPE, hashRecursiveEntity(keyConverter)));
			COMPLEX_ENTITY.setId(new DataTypeId(COMPLEX_TYPE, hashComplexEntity(keyConverter)));
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	private static String hashSimpleEntity(UidConverter keyConverter) throws NoSuchAlgorithmException {
		final MessageDigest hasher = hasher();
		hasher.update(SIMPLE_TYPE.getBytes());
		hasher.update(getField(SIMPLE_ENTITY, VALUE_FIELD).getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashNullReferenceEntity(UidConverter keyConverter) throws NoSuchAlgorithmException {
		final MessageDigest hasher = hasher();
		hasher.update(NULL_REFERENCE_TYPE.getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashRecursiveEntity(UidConverter keyConverter) throws NoSuchAlgorithmException {
		final MessageDigest hasher = hasher();
		hasher.update(RECURSIVE_TYPE.getBytes());
		hasher.update(getField(RECURSIVE_ENTITY, VALUE_FIELD).getBytes());
		hasher.update(RECURSIVE_SUBTYPE_UID.getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static String hashComplexEntity(UidConverter keyConverter) throws NoSuchAlgorithmException {
		final MessageDigest hasher = hasher();
		hasher.update(COMPLEX_TYPE.getBytes());
		hasher.update(hashSimpleEntity(keyConverter).getBytes());
		hasher.update(hashRecursiveEntity(keyConverter).getBytes());
		return keyConverter.convert(hasher.digest());
	}

	private static MessageDigest hasher() throws NoSuchAlgorithmException {
		return MessageDigest.getInstance("MD5");
	}

	private static String getField(Selector selector, String field) {
		return selector.get(field);
	}
}
