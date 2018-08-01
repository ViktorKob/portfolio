package net.thomas.portfolio.hbase_index.schema.processing;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.getClassSimpleName;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.getDeclaredFields;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.isArray;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.isEntityField;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.isSingleEntity;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.runTestOnAllEntityTypes;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.runTestOnAllEventTypes;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.runTestOnAllSelectorTypes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.TestSampleData;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class Entity2DataTypeConverterUnitTest {
	private Entity2DataTypeConverter converter;

	@Before
	public void setUpForTest() {
		converter = new Entity2DataTypeConverter();
	}

	@Test
	public void shouldConvertDataTypeTypeCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			getClassSimpleName(entity).equals(convertedEntity.getId().type);
		});
	}

	@Test
	public void shouldConvertUidsCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			entity.uid.equals(convertedEntity.getId().uid);
		});
	}

	@Test
	public void shouldConvertToDocumentForEvent() {
		runTestOnAllEventTypes((event) -> {
			final DataType convertedEntity = converter.convert(event);
			assertTrue(convertedEntity instanceof Document);
		});
	}

	@Test
	public void shouldConvertToRawDataTypeForEvent() {
		TestSampleData.runTestOnAllMetaTypes((metaEntity) -> {
			final DataType convertedEntity = converter.convert(metaEntity);
			assertTrue(convertedEntity instanceof RawDataType);
		});
	}

	@Test
	public void shouldConvertToSelectorForSelector() {
		runTestOnAllSelectorTypes((selector) -> {
			final DataType convertedEntity = converter.convert(selector);
			assertTrue(convertedEntity instanceof Selector);
		});
	}

	@Test
	public void shouldConvertTimeOfEventCorrectlyForEvent() {
		runTestOnAllEventTypes((event) -> {
			final DataType convertedEntity = converter.convert(event);
			event.timeOfEvent.equals(((Document) convertedEntity).getTimeOfEvent());
		});
	}

	@Test
	public void shouldConvertTimeOfInterceptionCorrectlyForEvent() {
		runTestOnAllEventTypes((event) -> {
			final DataType convertedEntity = converter.convert(event);
			event.timeOfInterception.equals(((Document) convertedEntity).getTimeOfInterception());
		});
	}

	@Test
	public void shouldConvertNonEntityFieldsCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			for (final Field field : getDeclaredFields(entity)) {
				if (!isEntityField(field)) {
					assertEquals(field.get(entity), convertedEntity.get(field.getName()));
				}
			}
		});
	}

	@Test
	public void shouldConvertEntityFieldsCorrectly() {
		runTestOnAllEntityTypes((entity) -> {
			final DataType convertedEntity = converter.convert(entity);
			for (final Field field : getDeclaredFields(entity)) {
				if (isArray(field)) {
					final Entity[] subEntities = (Entity[]) field.get(entity);
					final List<? extends DataType> subDataTypes = convertedEntity.get(field.getName());
					for (int i = 0; i < subEntities.length; i++) {
						final Entity expectedSubEntity = subEntities[i];
						final DataType actualSubEntity = subDataTypes.get(i);
						assertEquals(expectedSubEntity.uid, actualSubEntity.getId().uid);
					}
				} else if (isSingleEntity(field)) {
					final Entity expectedSubEntity = (Entity) field.get(entity);
					final DataType actualSubEntity = (DataType) convertedEntity.get(field.getName());
					assertEquals(expectedSubEntity.uid, actualSubEntity.getId().uid);
				}
			}
		});
	}
}