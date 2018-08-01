package net.thomas.portfolio.hbase_index.schema.processing;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.INSTANCE_OF_EACH_EVENT_TYPE;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.runTestOnAllEntityTypes;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.TestSampleData;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class SchemaIntrospectionUnitTest {
	private HbaseIndexSchema schema;

	@Before
	public void setUpForTest() {
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
			assertTrue(dataTypes.contains(TestSampleData.getClassSimpleName(entity)));
		});
	}
}
