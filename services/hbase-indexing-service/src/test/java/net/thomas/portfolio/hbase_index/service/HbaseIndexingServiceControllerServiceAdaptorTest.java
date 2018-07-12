package net.thomas.portfolio.hbase_index.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.string;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

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
			return new FakeSchemaForTesting();
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
		final Collection<Field> dataTypes = hbaseAdaptor.getFieldsForDataType(DOCUMENT_TYPE);
		assertEquals(schema.getFieldsForDataType(DOCUMENT_TYPE), dataTypes);
	}

	static class FakeSchemaForTesting implements HbaseIndexSchema {
		public FakeSchemaForTesting() {
			final Map<String, Collection<Field>> fields = new HashMap<>();
			fields.put(DOCUMENT_TYPE, asList(string("name")));
		}

		@Override
		public Collection<String> getDataTypes() {
			return new HashSet<>(asList(DOCUMENT_TYPE, SELECTOR_TYPE, RAW_DATA_TYPE, SIMPLE_REPRESENTABLE_TYPE));
		}

		@Override
		public Collection<String> getDocumentTypes() {
			return singleton(DOCUMENT_TYPE);
		}

		@Override
		public Collection<String> getSelectorTypes() {
			return new HashSet<>(asList(SELECTOR_TYPE, SIMPLE_REPRESENTABLE_TYPE));
		}

		@Override
		public Collection<String> getSimpleRepresentableTypes() {
			return singleton(SIMPLE_REPRESENTABLE_TYPE);
		}

		@Override
		public List<Field> getFieldsForDataType(String dataType) {
			return null;
		}

		@Override
		public List<DataTypeId> getSelectorSuggestions(String selectorString) {
			return null;
		}

		@Override
		public Set<String> getIndexableDocumentTypes(String selectorType) {
			return null;
		}

		@Override
		public Set<String> getIndexableRelations(String selectorType) {
			return null;
		}

		@Override
		public Set<String> getAllIndexableRelations() {
			return null;
		}

		@Override
		public Collection<Indexable> getIndexables(String selectorType) {
			return null;
		}

		@Override
		public Field getFieldForIndexable(Indexable indexable) {
			return null;
		}

		@Override
		public String calculateUid(String type, String simpleRep) {
			return null;
		}
	}
}