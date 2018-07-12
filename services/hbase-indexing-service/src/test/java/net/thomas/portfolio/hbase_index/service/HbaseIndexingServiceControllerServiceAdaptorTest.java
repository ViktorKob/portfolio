package net.thomas.portfolio.hbase_index.service;

import static java.util.Collections.emptyMap;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import java.util.Collection;
import java.util.List;
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

	private static final String TYPE = "TYPE";
	private static final String UID = "FFABCD";
	private static final DataTypeId DATA_TYPE_ID = new DataTypeId(TYPE, UID);
	private static final DataType ENTITY = new Selector(DATA_TYPE_ID, emptyMap());

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
	public void shouldGetDataTypeFromIndex() {
		when(index.getDataType(eq(DATA_TYPE_ID))).thenReturn(ENTITY);
		final DataType dataType = hbaseAdaptor.getDataType(DATA_TYPE_ID);
		assertEquals(ENTITY, dataType);
	}

	@Test
	public void shouldGetDataTypeFromCacheOnSecondQuery() {
		when(index.getDataType(eq(DATA_TYPE_ID))).thenReturn(ENTITY);
		hbaseAdaptor.getDataType(DATA_TYPE_ID);
		hbaseAdaptor.getDataType(DATA_TYPE_ID);
		verify(index, times(1)).getDataType(eq(DATA_TYPE_ID));
	}

	static class FakeSchemaForTesting implements HbaseIndexSchema {
		public FakeSchemaForTesting() {
		}

		@Override
		public Collection<String> getDataTypes() {
			return null;
		}

		@Override
		public Collection<String> getDocumentTypes() {
			return null;
		}

		@Override
		public Collection<String> getSelectorTypes() {
			return null;
		}

		@Override
		public Collection<String> getSimpleRepresentableTypes() {
			return null;
		}

		@Override
		public List<Field> getFieldsForDataType(String dataType) {
			return null;
		}

		@Override
		public String calculateUid(String type, String simpleRep) {
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
	}
}