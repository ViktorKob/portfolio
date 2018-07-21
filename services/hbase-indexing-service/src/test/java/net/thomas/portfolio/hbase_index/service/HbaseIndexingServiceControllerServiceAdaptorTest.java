package net.thomas.portfolio.hbase_index.service;

import static java.util.Collections.emptyMap;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.HbaseIndexingServiceProperties.loadHbaseIndexingConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import net.thomas.portfolio.service_commons.adaptors.Adaptors;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.port:18120", "eureka.client.registerWithEureka:false",
		"eureka.client.fetchRegistry:false" })
public class HbaseIndexingServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("hbase-indexing-service", 18120);

	@BeforeClass
	public static void setupContextPath() {
		loadHbaseIndexingConfigurationIntoProperties();
		loadDefaultServiceConfigurationIntoProperties();
		loadServicePathsIntoProperties();
	}

	@TestConfiguration
	static class ServiceBeansSetup {
		@Bean
		public HbaseIndexSchema getSchema() {
			return buildSchemaForTesting();
		}

		private HbaseIndexSchema buildSchemaForTesting() {
			final HbaseIndexSchemaBuilder builder = new HbaseIndexSchemaBuilder();
			builder.addFields(DOCUMENT_TYPE, fields(string("name"), dataType("reference", RAW_DATA_TYPE)));
			builder.addFields(RAW_DATA_TYPE, fields(string("name"), dataType("reference", SELECTOR_TYPE)));
			builder.addFields(SELECTOR_TYPE, fields(string("name")));
			builder.addDocumentTypes(DOCUMENT_TYPE);
			builder.addSelectorTypes(SELECTOR_TYPE, SIMPLE_REPRESENTABLE_TYPE);
			builder.addSimpleRepresentableTypes(SIMPLE_REPRESENTABLE_TYPE);
			return builder.build();
		}

		@Bean
		public HbaseIndex getIndex() {
			return mock(HbaseIndex.class);
		}

		@Bean
		public RestTemplate getRestTemplate() {
			return new RestTemplate();
		}
	}

	@Autowired
	private HbaseIndexSchema schema;
	@Autowired
	private HbaseIndex index;
	@Autowired
	private RestTemplate restTemplate;
	private Adaptors adaptors;

	@Before
	public void setUpController() throws Exception {
		reset(index);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		final HbaseIndexModelAdaptorImpl hbaseAdaptor = new HbaseIndexModelAdaptorImpl();
		hbaseAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
		adaptors = new Adaptors.Builder().setHbaseModelAdaptor(hbaseAdaptor)
			.build();
	}

	@Test
	public void shouldGetDataTypesFromSchema() {
		final Collection<String> dataTypes = adaptors.getDataTypes();
		assertEquals(schema.getDataTypes(), dataTypes);
	}

	@Test
	public void shouldGetDocumentTypesFromSchema() {
		final Collection<String> dataTypes = adaptors.getDocumentTypes();
		assertEquals(schema.getDocumentTypes(), dataTypes);
	}

	@Test
	public void shouldGetSelectorTypesFromSchema() {
		final Collection<String> dataTypes = adaptors.getSelectorTypes();
		assertEquals(schema.getSelectorTypes(), dataTypes);
	}

	@Test
	public void shouldBeSimpleRepresentable() {
		final boolean isSimpleRepresentable = adaptors.isSimpleRepresentable(SIMPLE_REPRESENTABLE_TYPE);
		assertTrue(isSimpleRepresentable);
	}

	@Test
	public void shouldNotBeSimpleRepresentable() {
		final boolean isSimpleRepresentable = adaptors.isSimpleRepresentable(DOCUMENT_TYPE);
		assertFalse(isSimpleRepresentable);
	}

	@Test
	public void shouldGetDataTypeFromIndex() {
		when(index.getDataType(eq(SOME_SELECTOR_ID))).thenReturn(SOME_ENTITY);
		final DataType dataType = adaptors.getDataType(SOME_SELECTOR_ID);
		assertEquals(SOME_ENTITY, dataType);
	}

	@Test
	public void shouldGetDataTypeFromCacheOnSecondQuery() {
		when(index.getDataType(eq(SOME_SELECTOR_ID))).thenReturn(SOME_ENTITY);
		adaptors.getDataType(SOME_SELECTOR_ID);
		adaptors.getDataType(SOME_SELECTOR_ID);
		verify(index, times(1)).getDataType(eq(SOME_SELECTOR_ID));
	}

	@Test
	public void shouldGetIndexedRelationsFromSchema() {
		final Collection<String> dataTypes = adaptors.getAllIndexedRelations();
		assertEquals(schema.getAllIndexableRelations(), dataTypes);
	}

	@Test
	public void shouldGetDataTypeFieldsFromSchema() {
		final Fields dataTypes = adaptors.getFieldsForDataType(DOCUMENT_TYPE);
		assertEquals(schema.getFieldsForDataType(DOCUMENT_TYPE), dataTypes);
	}

	private static final String SELECTOR_TYPE = "SELECTOR_TYPE";
	private static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";
	private static final String RAW_DATA_TYPE = "RAW_TYPE";
	private static final String SIMPLE_REPRESENTABLE_TYPE = "SIMPLE_REP";
	private static final DataTypeId SOME_SELECTOR_ID = new DataTypeId(SELECTOR_TYPE, "FFABCD");
	private static final DataType SOME_ENTITY = new Selector(SOME_SELECTOR_ID, emptyMap());
}