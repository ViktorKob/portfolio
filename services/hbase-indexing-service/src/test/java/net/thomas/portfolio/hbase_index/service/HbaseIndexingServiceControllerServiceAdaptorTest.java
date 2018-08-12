package net.thomas.portfolio.hbase_index.service;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static net.thomas.portfolio.services.Service.loadServicePathsIntoProperties;
import static net.thomas.portfolio.services.configuration.DefaultServiceParameters.loadDefaultServiceConfigurationIntoProperties;
import static net.thomas.portfolio.services.configuration.HbaseIndexingServiceProperties.loadHbaseIndexingConfigurationIntoProperties;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Source.APPLE;
import static net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.StatisticsPeriod.INFINITY;
import static net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId.NULL_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

import java.util.Collection;
import java.util.List;

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
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Reference;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Entities;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndex;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.PositiveIntegerFieldSimpleRepParser;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;

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
			builder.addFields(SIMPLE_REPRESENTABLE_TYPE, fields(string("name")));
			builder.addDocumentTypes(DOCUMENT_TYPE);
			builder.addSelectorTypes(SELECTOR_TYPE, SIMPLE_REPRESENTABLE_TYPE);
			builder.addSimpleRepresentableTypes(SIMPLE_REPRESENTABLE_TYPE);
			builder.addIndexable(SELECTOR_TYPE, "Path", DOCUMENT_TYPE, "Field");
			builder.addSimpleRepresentationParser(SIMPLE_REPRESENTABLE_TYPE, "name", PositiveIntegerFieldSimpleRepParser.class);
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
	public void shouldBeSelector() {
		final boolean isSelector = adaptors.isSelector(SELECTOR_TYPE);
		assertTrue(isSelector);
	}

	@Test
	public void shouldNotBeSelector() {
		final boolean isSelector = adaptors.isSelector(DOCUMENT_TYPE);
		assertFalse(isSelector);
	}

	@Test
	public void shouldBeDocument() {
		final boolean isDocument = adaptors.isDocument(DOCUMENT_TYPE);
		assertTrue(isDocument);
	}

	@Test
	public void shouldNotBeDocument() {
		final boolean isDocument = adaptors.isDocument(SELECTOR_TYPE);
		assertFalse(isDocument);
	}

	@Test
	public void shouldGetDataTypeFromIndex() {
		when(index.getDataType(eq(SOME_ID))).thenReturn(SOME_ENTITY);
		final DataType dataType = adaptors.getDataType(SOME_ID);
		assertEquals(SOME_ENTITY, dataType);
	}

	@Test
	public void shouldGetDataTypeFromCacheOnSecondQuery() {
		when(index.getDataType(eq(SOME_ID))).thenReturn(SOME_ENTITY);
		adaptors.getDataType(SOME_ID);
		adaptors.getDataType(SOME_ID);
		verify(index, times(1)).getDataType(eq(SOME_ID));
	}

	@Test
	public void shouldGetNullWhenDataTypeNotPresent() {
		final DataType dataType = adaptors.getDataType(SOME_ID);
		assertNull(dataType);
	}

	@Test
	public void shouldGetIndexedDocumentTypesForSelectorFromSchema() {
		final Collection<String> documentTypes = adaptors.getIndexedDocumentTypes(SELECTOR_TYPE);
		assertEquals(schema.getIndexableDocumentTypes(SELECTOR_TYPE), documentTypes);
	}

	@Test
	public void shouldGetIndexedRelationsForSelectorFromSchema() {
		final Collection<String> relations = adaptors.getIndexedRelations(SELECTOR_TYPE);
		assertEquals(schema.getIndexableRelations(SELECTOR_TYPE), relations);
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

	@Test
	public void shouldGetIdFromSimpleRepFromSchema() {
		final DataTypeId id = adaptors.getIdFromSimpleRep(SIMPLE_REPRESENTABLE_TYPE, SIMPLE_REPRESENTATION);
		assertEquals(schema.parseToUid(SIMPLE_REPRESENTABLE_TYPE, SIMPLE_REPRESENTATION), id.uid);
	}

	@Test
	public void shouldGetNullIdFromSimpleRepFromSchemaWhenNotParsable() {
		final DataTypeId id = adaptors.getIdFromSimpleRep(SELECTOR_TYPE, INVALID_SIMPLE_REPRESENTATION);
		assertEquals(NULL_ID, id);
	}

	@Test
	public void shouldLookupSuggestions() {
		final List<DataTypeId> suggestions = adaptors.getSelectorSuggestions(SIMPLE_REPRESENTATION);
		assertEquals(SIMPLE_REPRESENTABLE_TYPE, first(suggestions).type);
	}

	@Test
	public void shouldReturnNullSuggestionWhenNotParsingPossible() {
		final List<DataTypeId> suggestions = adaptors.getSelectorSuggestions(INVALID_SIMPLE_REPRESENTATION);
		assertNull(first(suggestions).uid);
	}

	@Test
	public void shouldLookupSample() {
		when(index.getSamples(eq(SELECTOR_TYPE), anyInt())).thenReturn(new Entities(singleton(SOME_ENTITY)));
		final Entities entities = adaptors.getSamples(SELECTOR_TYPE, 1);
		assertEquals(SOME_ENTITY.getId(), first(entities).getId());
	}

	@Test
	public void shouldReturn404WhenThereAreNoSamples() {
		when(index.getSamples(eq(SELECTOR_TYPE), anyInt())).thenReturn(new Entities());
		final Entities entities = adaptors.getSamples(SELECTOR_TYPE, 1);
		assertNull(entities);
	}

	@Test
	public void shouldGetReferencesForDocument() {
		final Reference reference = new Reference(APPLE, "id", emptySet());
		when(index.getReferences(eq(SOME_ID))).thenReturn(new References(singleton(reference)));
		final References references = adaptors.getReferences(SOME_ID);
		assertEquals(reference, first(references));
	}

	@Test
	public void shouldGetStatisticsForSelector() {
		final Long expected = 1L;
		when(index.getStatistics(eq(SOME_ID))).thenReturn(new Statistics(singletonMap(INFINITY, expected)));
		final Statistics statistics = adaptors.getStatistics(SOME_ID);
		assertEquals(expected, statistics.get(INFINITY));
	}

	@Test
	public void shouldLookupSelectorInInvertedIndex() {
		final Indexable indexable = new Indexable(SELECTOR_TYPE, "Path", DOCUMENT_TYPE, "Field");
		when(index.invertedIndexLookup(eq(SOME_ID), eq(indexable)))
			.thenReturn(new DocumentInfos(asList(new DocumentInfo(SOME_ID, new Timestamp(1000L), new Timestamp(2000L)))));
		final DocumentInfos infos = adaptors
			.lookupSelectorInInvertedIndex(new InvertedIndexLookupRequest(SOME_ID, new LegalInformation(), new Bounds(), emptySet(), emptySet()));
		assertEquals(SOME_ID, first(infos).getId());
	}

	@Test
	public void shouldReturnEmptyContainerWhenIdIsUnknown() {
		final DocumentInfos infos = adaptors
			.lookupSelectorInInvertedIndex(new InvertedIndexLookupRequest(SOME_ID, new LegalInformation(), new Bounds(), emptySet(), emptySet()));
		assertNotNull(infos);
		assertFalse(infos.hasData());
	}

	private DataTypeId first(List<DataTypeId> ids) {
		return ids.get(0);
	}

	private DataType first(Entities entities) {
		return entities.getEntities()
			.iterator()
			.next();
	}

	private Reference first(References references) {
		return references.getReferences()
			.iterator()
			.next();
	}

	private DocumentInfo first(DocumentInfos infos) {
		return infos.getInfos()
			.iterator()
			.next();
	}

	private static final String SELECTOR_TYPE = "SELECTOR_TYPE";
	private static final String DOCUMENT_TYPE = "DOCUMENT_TYPE";
	private static final String RAW_DATA_TYPE = "RAW_TYPE";
	private static final String SIMPLE_REPRESENTABLE_TYPE = "SIMPLE_REP";
	private static final String SIMPLE_REPRESENTATION = "1234";
	private static final String INVALID_SIMPLE_REPRESENTATION = "NotANumber";
	private static final DataTypeId SOME_ID = new DataTypeId(SELECTOR_TYPE, "FFABCD");
	private static final DataType SOME_ENTITY = new Selector(SOME_ID, emptyMap());
}