package net.thomas.portfolio.service_commons.adaptors;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.integer;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.UsageAdaptorImpl;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.UnauthorizedAccessException;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.DocumentUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.EntityUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.AuditUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.AuditUrls.CheckUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.AuditUrls.LogUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.HistoryUrls;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.References;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Statistics;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfo;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DocumentInfos;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.legal.Legality;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivities;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

/***
 * Individual adaptor implementations are tested through the Adaptors class since these should
 * always be in sync.
 *
 * Hateoas unwrappers are also included, since these are not worth mocking
 */
public class AdaptorsUnitTest {
	private static final String SOME_UID = "ABCD";
	private static final String SOME_DOCUMENT_TYPE = "SomeDocumentType";
	private static final String SOME_SELECTOR_TYPE = "SomeSelectorType";
	private static final DataTypeId SOME_DOCUMENT_ID = new DataTypeId(SOME_DOCUMENT_TYPE, SOME_UID);
	private static final DataTypeId SOME_SELECTOR_ID = new DataTypeId(SOME_SELECTOR_TYPE, SOME_UID);
	private static final Fields SOME_DOCUMENT_FIELDS = fields(dataType("SomeField", SOME_SELECTOR_TYPE));
	private static final Fields SOME_SELECTOR_FIELDS = fields(integer("SomeOtherField"));
	private static final String SOME_SIMPLE_REPRESENTATION = "SimpleRepresentation";
	private static final String SOME_URL_STRING = "SomeUrl";
	private static final String SOME_SCHEMA_URL = "SomeSchemaUrl";
	private static final Legality SOME_LEGALITY = Legality.LEGAL;
	private static final Boolean SUCCESS = true;
	private static final String SOME_RENDERED_STRING = "Render";
	private static final Document SOME_DOCUMENT = new Document(SOME_DOCUMENT_ID);
	private static final Selector SOME_SELECTOR = new Selector(SOME_SELECTOR_ID);
	private static final References SOME_REFERENCES = new References();
	private static final Statistics SOME_STATISTICS = new Statistics();
	private static final DocumentInfo SOME_DOCUMENT_INFO = new DocumentInfo(SOME_DOCUMENT_ID, null, null);
	private static final InvertedIndexLookupRequest SOME_REQUEST = new InvertedIndexLookupRequest(SOME_SELECTOR_ID, null, null, emptySet(), emptySet());

	private DataTypeId someEntityId;
	private AnalyticalKnowledge someAnalyticalKnowledge;
	private LegalInformation someLegalInfo;
	private UsageActivity someUsageActivity;
	private UsageActivities someUsageActivities;
	private Bounds someBounds;

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient httpClient;
	private HbaseIndexSchemaImpl sampleSchema;

	private AnalyticsAdaptorImpl analyticsAdaptor;
	private HbaseIndexModelAdaptorImpl hbaseModelAdaptor;
	private LegalAdaptorImpl legalAdaptor;
	private RenderingAdaptorImpl renderingAdaptor;
	private UsageAdaptorImpl usageAdaptor;
	private Adaptors adaptors;

	@Before
	public void setUpParameterMocks() {
		someEntityId = mock(DataTypeId.class);
		someAnalyticalKnowledge = mock(AnalyticalKnowledge.class);
		someLegalInfo = mock(LegalInformation.class);
		someUsageActivity = mock(UsageActivity.class);
		someUsageActivities = mock(UsageActivities.class);
		someBounds = mock(Bounds.class);
	}

	@Before
	public void setUpAdaptors() throws Exception {
		sampleSchema = setupSampleSchema();
		urlLibrary = buildUrlLibraryMock();
		httpClient = mock(HttpRestClient.class);
		when(urlLibrary.schema()).thenReturn(SOME_SCHEMA_URL);
		when(httpClient.loadUrlAsObject(eq(SOME_SCHEMA_URL), eq(GET), any())).thenReturn(new Resource<>(sampleSchema));
		analyticsAdaptor = new AnalyticsAdaptorImpl();
		hbaseModelAdaptor = new HbaseIndexModelAdaptorImpl();
		legalAdaptor = new LegalAdaptorImpl();
		renderingAdaptor = new RenderingAdaptorImpl();
		usageAdaptor = new UsageAdaptorImpl();

		adaptors = new Adaptors.Builder().setAnalyticsAdaptor(analyticsAdaptor)
				.setHbaseModelAdaptor(hbaseModelAdaptor)
				.setLegalAdaptor(legalAdaptor)
				.setRenderingAdaptor(renderingAdaptor)
				.setUsageAdaptor(usageAdaptor)
				.build();

		initializeAdaptorsWithTimeout(urlLibrary, httpClient, 2000, SECONDS);
	}

	private HbaseIndexSchemaImpl setupSampleSchema() {
		final HbaseIndexSchemaImpl schema = new HbaseIndexSchemaImpl();
		final Map<String, Fields> dataTypes = new HashMap<>();
		dataTypes.put(SOME_DOCUMENT_TYPE, SOME_DOCUMENT_FIELDS);
		dataTypes.put(SOME_SELECTOR_TYPE, SOME_SELECTOR_FIELDS);
		schema.setDataTypeFields(dataTypes);
		schema.setDocumentTypes(singleton(SOME_DOCUMENT_TYPE));
		schema.setSelectorTypes(singleton(SOME_SELECTOR_TYPE));
		schema.setSimpleRepresentableTypes(singleton(SOME_SELECTOR_TYPE));
		return schema;
	}

	private void initializeAdaptorsWithTimeout(PortfolioUrlLibrary urlLibrary, HttpRestClient httpClient, long time, TimeUnit unit) throws Exception {
		newSingleThreadExecutor().submit(() -> {
			analyticsAdaptor.initialize(urlLibrary, httpClient);
			hbaseModelAdaptor.initialize(urlLibrary, httpClient);
			legalAdaptor.initialize(urlLibrary, httpClient);
			renderingAdaptor.initialize(urlLibrary, httpClient);
			usageAdaptor.initialize(urlLibrary, httpClient);
		}).get(time, unit);
	}

	@Test
	public void shouldGetAnalyticalKnowledge() {
		when(urlLibrary.selectors().knowledge(eq(someEntityId))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(someAnalyticalKnowledge));
		final AnalyticalKnowledge knowledge = adaptors.getKnowledge(someEntityId);
		assertEquals(someAnalyticalKnowledge, knowledge);
	}

	@Test(expected = UnauthorizedAccessException.class)
	public void shouldRaiseExceptionWhenNotAuthorizedForSchema() {
		when(httpClient.loadUrlAsObject(eq(SOME_SCHEMA_URL), eq(GET), any())).thenThrow(UnauthorizedAccessException.class);
		new HbaseIndexModelAdaptorImpl().initialize(urlLibrary, httpClient);
	}

	@Test
	public void shouldSilentlyRetryWhenCatchingRuntimeExceptionWhileFetchingSchema() {
		when(httpClient.loadUrlAsObject(eq(SOME_SCHEMA_URL), eq(GET), any())).thenThrow(RuntimeException.class).thenReturn(new Resource<>(sampleSchema));
		new HbaseIndexModelAdaptorImpl().initialize(urlLibrary, httpClient);
	}

	@Test
	public void shouldGetDataTypesDirectlyFromSchema() {
		final Collection<String> dataTypes = adaptors.getDataTypes();
		assertEquals(sampleSchema.getDataTypes(), dataTypes);
	}

	@Test
	public void shouldGetDocumentTypesDirectlyFromSchema() {
		final Collection<String> dataTypes = adaptors.getDocumentTypes();
		assertEquals(sampleSchema.getDocumentTypes(), dataTypes);
	}

	@Test
	public void shouldGetSelectorTypesDirectlyFromSchema() {
		final Collection<String> dataTypes = adaptors.getSelectorTypes();
		assertEquals(sampleSchema.getSelectorTypes(), dataTypes);
	}

	@Test
	public void shouldGetSimpleRepresentableStatusDirectlyFromSchema() {
		assertTrue(adaptors.isSimpleRepresentable(SOME_SELECTOR_TYPE));
		assertFalse(adaptors.isSimpleRepresentable(SOME_DOCUMENT_TYPE));
	}

	@Test
	public void shouldGetSelectorStatusDirectlyFromSchema() {
		assertTrue(adaptors.isSelector(SOME_SELECTOR_TYPE));
		assertFalse(adaptors.isSelector(SOME_DOCUMENT_TYPE));
	}

	@Test
	public void shouldGetDocumentStatusDirectlyFromSchema() {
		assertTrue(adaptors.isDocument(SOME_DOCUMENT_TYPE));
		assertFalse(adaptors.isDocument(SOME_SELECTOR_TYPE));
	}

	@Test
	public void shouldGetFieldsForDocumentDirectlyFromSchema() {
		assertEquals(sampleSchema.getFieldsForDataType(SOME_DOCUMENT_TYPE), adaptors.getFieldsForDataType(SOME_DOCUMENT_TYPE));
	}

	@Test
	public void shouldGetFieldsForSelectorDirectlyFromSchema() {
		assertEquals(sampleSchema.getFieldsForDataType(SOME_SELECTOR_TYPE), adaptors.getFieldsForDataType(SOME_SELECTOR_TYPE));
	}

	@Test
	public void shouldGetSelectorSuggestions() {
		when(urlLibrary.selectors().suggestions(eq(SOME_SIMPLE_REPRESENTATION))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resources<>(singleton(SOME_SELECTOR)));
		final List<Selector> selectors = adaptors.getSelectorSuggestions(SOME_SIMPLE_REPRESENTATION);
		assertSame(SOME_SELECTOR, selectors.get(0));
	}

	@Test
	public void shouldGetSelectorFromRepresentation() {
		when(urlLibrary.selectors().fromSimpleRepresentation(eq(SOME_SELECTOR_TYPE), eq(SOME_SIMPLE_REPRESENTATION))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_SELECTOR));
		final Selector selector = adaptors.getFromSimpleRep(SOME_SELECTOR_TYPE, SOME_SIMPLE_REPRESENTATION);
		assertSame(SOME_SELECTOR, selector);
	}

	@Test
	public void shouldGetDocumentById() {
		when(urlLibrary.entities().lookup(eq(SOME_DOCUMENT_ID))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_DOCUMENT));
		final DataType selector = adaptors.getDataType(SOME_DOCUMENT_ID);
		assertSame(SOME_DOCUMENT, selector);
	}

	@Test
	public void shouldGetSelectorById() {
		when(urlLibrary.entities().lookup(eq(SOME_SELECTOR_ID))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_SELECTOR));
		final DataType selector = adaptors.getDataType(SOME_SELECTOR_ID);
		assertSame(SOME_SELECTOR, selector);
	}

	@Test
	public void shouldGracefullySurviceUnknownEntity() {
		when(urlLibrary.entities().lookup(eq(SOME_DOCUMENT_ID))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(null);
		final DataType selector = adaptors.getDataType(SOME_DOCUMENT_ID);
		assertNull(selector);
	}

	@Test
	public void shouldGetReferencesForDocument() {
		when(urlLibrary.documents().references(eq(SOME_DOCUMENT_ID))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_REFERENCES));
		final References response = adaptors.getReferences(SOME_DOCUMENT_ID);
		assertSame(SOME_REFERENCES, response);
	}

	@Test
	public void shouldGetStatisticsForSelector() {
		when(urlLibrary.selectors().statistics(eq(SOME_SELECTOR_ID))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_STATISTICS));
		final Statistics response = adaptors.getStatistics(SOME_SELECTOR_ID);
		assertSame(SOME_STATISTICS, response);
	}

	@Test
	public void shouldGetDocumentsBasedOnInvertedIndexLookup() {
		when(urlLibrary.selectors().invertedIndex(same(SOME_REQUEST))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resources<>(singleton(SOME_DOCUMENT_INFO)));
		final DocumentInfos response = adaptors.lookupSelectorInInvertedIndex(SOME_REQUEST);
		assertSame(SOME_DOCUMENT_INFO, response.getInfos().iterator().next());
	}

	@Test
	public void shouldGetLegalityOfInvertedIndexLookup() {
		when(urlLibrary.selectors().audit().check().invertedIndex(eq(someEntityId), eq(someLegalInfo))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_LEGALITY));
		final Legality legality = adaptors.checkLegalityOfInvertedIndexLookup(someEntityId, someLegalInfo);
		assertEquals(SOME_LEGALITY, legality);
	}

	@Test
	public void shouldGetLegalityOfStatisticsLookup() {
		when(urlLibrary.selectors().audit().check().statistics(eq(someEntityId), eq(someLegalInfo))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_LEGALITY));
		final Legality legality = adaptors.checkLegalityOfStatisticsLookup(someEntityId, someLegalInfo);
		assertEquals(SOME_LEGALITY, legality);
	}

	@Test
	public void shouldPostInvertedIndexLookup() {
		when(urlLibrary.selectors().audit().log().invertedIndex(eq(someEntityId), eq(someLegalInfo))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(POST))).thenReturn(SUCCESS);
		final Boolean success = adaptors.auditLogInvertedIndexLookup(someEntityId, someLegalInfo);
		assertEquals(SUCCESS, success);
	}

	@Test
	public void shouldPostStatisticsLookup() {
		when(urlLibrary.selectors().audit().log().statistics(eq(someEntityId), eq(someLegalInfo))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(POST))).thenReturn(SUCCESS);
		final Boolean success = adaptors.auditLogStatisticsLookup(someEntityId, someLegalInfo);
		assertEquals(SUCCESS, success);
	}

	@Test
	public void shouldGetTextRender() {
		when(urlLibrary.entities().render().text(eq(someEntityId))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_RENDERED_STRING));
		final String text = adaptors.renderAsText(someEntityId);
		assertEquals(SOME_RENDERED_STRING, text);
	}

	@Test
	public void shouldGetSimpleRepresentationRender() {
		when(urlLibrary.selectors().render().simpleRepresentation(eq(someEntityId))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_RENDERED_STRING));
		final String simpleRepresentation = adaptors.renderAsSimpleRepresentation(someEntityId);
		assertEquals(SOME_RENDERED_STRING, simpleRepresentation);
	}

	@Test
	public void shouldGetHtmlRender() {
		when(urlLibrary.entities().render().html(eq(someEntityId))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(SOME_RENDERED_STRING));
		final String html = adaptors.renderAsHtml(someEntityId);
		assertEquals(SOME_RENDERED_STRING, html);
	}

	@Test
	public void shouldGetUsageActivities() {
		when(urlLibrary.documents().usageActivities(eq(someEntityId), eq(someBounds))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(someUsageActivities));
		final UsageActivities activities = adaptors.fetchUsageActivities(someEntityId, someBounds);
		assertEquals(someUsageActivities, activities);
	}

	@Test
	public void shouldPostUsageActivity() {
		when(urlLibrary.documents().usageActivities(eq(someEntityId), eq(someUsageActivity))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(POST), any())).thenReturn(new Resource<>(someUsageActivity));
		final UsageActivity activity = adaptors.storeUsageActivity(someEntityId, someUsageActivity);
		assertEquals(someUsageActivity, activity);
	}

	public PortfolioUrlLibrary buildUrlLibraryMock() {
		final EntityUrls entities = buildEntitiesMock();
		final DocumentUrls documents = buildDocumentsMock();
		final SelectorUrls selectors = buildSelectorsMock();
		final PortfolioUrlLibrary library = mock(PortfolioUrlLibrary.class);
		when(library.entities()).thenReturn(entities);
		when(library.documents()).thenReturn(documents);
		when(library.selectors()).thenReturn(selectors);
		return library;
	}

	private EntityUrls buildEntitiesMock() {
		final EntityUrls.RenderUrls render = mock(EntityUrls.RenderUrls.class);
		final EntityUrls entities = mock(EntityUrls.class);
		when(entities.render()).thenReturn(render);
		return entities;
	}

	private DocumentUrls buildDocumentsMock() {
		final DocumentUrls.RenderUrls render = mock(DocumentUrls.RenderUrls.class);
		final DocumentUrls documents = mock(DocumentUrls.class);
		when(documents.render()).thenReturn(render);
		return documents;
	}

	private SelectorUrls buildSelectorsMock() {
		final SelectorUrls.RenderUrls render = mock(SelectorUrls.RenderUrls.class);
		final AuditUrls audit = buildAuditMock();
		final HistoryUrls history = mock(HistoryUrls.class);
		final SelectorUrls selectors = mock(SelectorUrls.class);
		when(selectors.render()).thenReturn(render);
		when(selectors.audit()).thenReturn(audit);
		when(selectors.history()).thenReturn(history);
		return selectors;
	}

	private AuditUrls buildAuditMock() {
		final CheckUrls check = mock(CheckUrls.class);
		final LogUrls log = mock(LogUrls.class);
		final AuditUrls audit = mock(AuditUrls.class);
		when(audit.check()).thenReturn(check);
		when(audit.log()).thenReturn(log);
		return audit;
	}
}