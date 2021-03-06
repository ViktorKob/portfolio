package net.thomas.portfolio.hbase_index.schema.processing;

import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.getClassSimpleName;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.getRelevantFields;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.isArray;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.isEntityField;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.isSingleEntity;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.runTestOnAllEntityTypes;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.runTestOnAllEventTypes;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.runTestOnAllSelectorTypes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class Entity2DataTypeConverterUnitTest {
	private static final boolean TEST_DONE = true;

	private Entity2DataTypeConverter converter;

	@Before
	public void setUpForTest() {
		converter = new Entity2DataTypeConverter();
	}

	@Test
	public void shouldConvertDataTypeTypeCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			assertTrue(getClassSimpleName(entity).equals(convertedEntity.getId().type));
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertUidsCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			assertTrue(entity.uid.equals(convertedEntity.getId().uid));
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertToDocumentForEvent() {
		runTestOnAllEventTypes((event) -> {
			final DataType convertedEntity = converter.convert(event);
			assertTrue(convertedEntity instanceof Document);
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertToRawDataTypeForEvent() {
		EntitySamplesForTesting.runTestOnAllMetaTypes((metaEntity) -> {
			final DataType convertedEntity = converter.convert(metaEntity);
			assertTrue(convertedEntity instanceof RawDataType);
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertToSelectorForSelector() {
		runTestOnAllSelectorTypes((selector) -> {
			final DataType convertedEntity = converter.convert(selector);
			assertTrue(convertedEntity instanceof Selector);
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertTimeOfEventCorrectlyForEvent() {
		runTestOnAllEventTypes((event) -> {
			final DataType convertedEntity = converter.convert(event);
			assertTrue(event.timeOfEvent.equals(((Document) convertedEntity).getTimeOfEvent()));
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertTimeOfInterceptionCorrectlyForEvent() {
		runTestOnAllEventTypes((event) -> {
			final DataType convertedEntity = converter.convert(event);
			assertTrue(event.timeOfInterception.equals(((Document) convertedEntity).getTimeOfInterception()));
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertNonEntityFieldsCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			for (final Field field : getRelevantFields(entity)) {
				if (!isEntityField(field)) {
					assertEquals(field.get(entity), convertedEntity.get(field.getName()));
				}
			}
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	@Test
	public void shouldConvertEntityFieldsCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			for (final Field field : getRelevantFields(entity)) {
				if (isArray(field)) {
					assertSubEntitiesHavePairwiseIdenticalUids((Entity[]) field.get(entity), convertedEntity.get(field.getName()));
				} else if (isSingleEntity(field)) {
					assertHaveSameUid((Entity) field.get(entity), (DataType) convertedEntity.get(field.getName()));
				}
			}
		});
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	private void assertSubEntitiesHavePairwiseIdenticalUids(final Entity[] subEntities, final List<? extends DataType> subDataTypes) {
		for (int i = 0; i < subEntities.length; i++) {
			assertHaveSameUid(subEntities[i], subDataTypes.get(i));
		}
	}

	private void assertHaveSameUid(final Entity expectedSubEntity, final DataType actualSubEntity) {
		assertEquals(expectedSubEntity.uid, actualSubEntity.getId().uid);
	}
}