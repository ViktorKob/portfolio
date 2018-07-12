package net.thomas.portfolio.hbase_index.service;

import static java.util.Collections.emptyMap;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.dataType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import net.thomas.portfolio.service_commons.services.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_testing.TestCommunicationWiringTool;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = DEFINED_PORT, properties = { "server.name=hbase-indexing-service", "server.port:18120",
		"eureka.client.registerWithEureka:false", "eureka.client.fetchRegistry:false" })
public class HbaseIndexingServiceControllerServiceAdaptorTest {
	private static final TestCommunicationWiringTool COMMUNICATION_WIRING = new TestCommunicationWiringTool("hbase-indexing-service", 18120);

	private static final String SELECTOR_TYPE = "SELECTOR_TYPE";
	private static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";
	private static final String RAW_DATA_TYPE = "RAW_TYPE";
	private static final String SIMPLE_REPRESENTABLE_TYPE = "SIMPLE_REP";
	private static final String UID = "FFABCD";
	private static final DataTypeId SELECTOR_ID = new DataTypeId(SELECTOR_TYPE, UID);
	private static final DataType ENTITY = new Selector(SELECTOR_ID, emptyMap());

	@TestConfiguration
	static class ServiceBeansSetup {
		@Bean
		public HbaseIndexSchema getSchema() {
			return buildSchemaForTesting();
		}

		private HbaseIndexSchema buildSchemaForTesting() {
			final HbaseIndexSchemaBuilder builder = new HbaseIndexSchemaBuilder();
			builder.addField(DOCUMENT_TYPE, fields(string("name"), dataType("reference", RAW_DATA_TYPE)));
			builder.addField(RAW_DATA_TYPE, fields(string("name"), dataType("reference", SELECTOR_TYPE)));
			builder.addField(SELECTOR_TYPE, fields(string("name")));
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
	private HbaseIndexModelAdaptorImpl hbaseAdaptor;

	@Before
	public void setUpController() throws Exception {
		reset(index);
		COMMUNICATION_WIRING.setRestTemplate(restTemplate);
		hbaseAdaptor = new HbaseIndexModelAdaptorImpl();
		hbaseAdaptor.initialize(COMMUNICATION_WIRING.setupMockAndGetHttpClient());
	}

	@Test
	public void shouldGetDataTypesFromSchema() {
		final Collection<String> dataTypes = hbaseAdaptor.getDataTypes();
		assertEquals(schema.getDataTypes(), dataTypes);
	}

	@Test
	public void shouldGetDocumentTypesFromSchema() {
		final Collection<String> dataTypes = hbaseAdaptor.getDocumentTypes();
		assertEquals(schema.getDocumentTypes(), dataTypes);
	}

	@Test
	public void shouldGetSelectorTypesFromSchema() {
		final Collection<String> dataTypes = hbaseAdaptor.getSelectorTypes();
		assertEquals(schema.getSelectorTypes(), dataTypes);
	}

	@Test
	public void shouldBeSimpleRepresentable() {
		final boolean isSimpleRepresentable = hbaseAdaptor.isSimpleRepresentable(SIMPLE_REPRESENTABLE_TYPE);
		assertTrue(isSimpleRepresentable);
	}

	@Test
	public void shouldNotBeSimpleRepresentable() {
		final boolean isSimpleRepresentable = hbaseAdaptor.isSimpleRepresentable(DOCUMENT_TYPE);
		assertFalse(isSimpleRepresentable);
	}

	@Test
	public void shouldGetDataTypeFromIndex() {
		when(index.getDataType(eq(SELECTOR_ID))).thenReturn(ENTITY);
		final DataType dataType = hbaseAdaptor.getDataType(SELECTOR_ID);
		assertEquals(ENTITY, dataType);
	}

	@Test
	public void shouldGetDataTypeFromCacheOnSecondQuery() {
		when(index.getDataType(eq(SELECTOR_ID))).thenReturn(ENTITY);
		hbaseAdaptor.getDataType(SELECTOR_ID);
		hbaseAdaptor.getDataType(SELECTOR_ID);
		verify(index, times(1)).getDataType(eq(SELECTOR_ID));
	}

	@Test
	public void shouldGetIndexedRelationsFromSchema() {
		final Collection<String> dataTypes = hbaseAdaptor.getAllIndexedRelations();
		assertEquals(schema.getAllIndexableRelations(), dataTypes);
	}

	@Test
	public void shouldGetDataTypeFieldsFromSchema() {
		final Fields dataTypes = hbaseAdaptor.getFieldsForDataType(DOCUMENT_TYPE);
		assertEquals(schema.getFieldsForDataType(DOCUMENT_TYPE), dataTypes);
	}
}