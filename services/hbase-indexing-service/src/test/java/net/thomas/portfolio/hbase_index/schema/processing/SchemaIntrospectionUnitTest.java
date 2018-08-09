package net.thomas.portfolio.hbase_index.schema.processing;

import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.INSTANCE_OF_EACH_EVENT_TYPE;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.getClassSimpleName;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.runTestOnAllEntityTypes;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.runTestOnAllEventTypes;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.runTestOnAllSelectorTypes;
import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.runTestOnAllSimpleRepresentableSelectorTypes;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class SchemaIntrospectionUnitTest {
	private static HbaseIndexSchema schema;

	@BeforeClass
	public static void setUpForTest() {
		final SchemaIntrospection schemaIntrospection = new SchemaIntrospection();
		for (final Event event : INSTANCE_OF_EACH_EVENT_TYPE) {
			schemaIntrospection.examine(event.getClass());
		}
		schema = schemaIntrospection.describeSchema();
	}

	@Test
	public void shouldContainAllDataTypes() {
		final Collection<String> dataTypes = schema.getDataTypes();
		runTestOnAllEntityTypes((entity) -> {
			assertTrue(dataTypes.contains(getClassSimpleName(entity)));
		});
	}

	@Test
	public void shouldContainAllSelectorTypes() {
		final Collection<String> dataTypes = schema.getSelectorTypes();
		runTestOnAllSelectorTypes((entity) -> {
			assertTrue(dataTypes.contains(getClassSimpleName(entity)));
		});
	}

	@Test
	public void shouldContainAllDocumentTypes() {
		final Collection<String> dataTypes = schema.getDocumentTypes();
		runTestOnAllEventTypes((entity) -> {
			assertTrue(dataTypes.contains(getClassSimpleName(entity)));
		});
	}

	@Test
	public void shouldContainAllSimpleRepresentableTypes() {
		final Collection<String> dataTypes = schema.getSimpleRepresentableTypes();
		runTestOnAllSimpleRepresentableSelectorTypes((selector) -> {
			assertTrue(dataTypes.contains(getClassSimpleName(selector)));
		});
	}
}