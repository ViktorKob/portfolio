package net.thomas.portfolio.service_commons.adaptors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resource;

import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.UsageAdaptorImpl;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.DocumentUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.EntityUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.AuditUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.AuditUrls.CheckUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.AuditUrls.LogUrls;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls.HistoryUrls;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
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
	private static final String SOME_URL_STRING = "SomeUrl";
	private static final Legality SOME_LEGALITY = Legality.LEGAL;
	private static final Boolean SUCCESS = true;
	private static final String SOME_RENDERED_STRING = "Render";

	private DataTypeId someEntityId;
	private AnalyticalKnowledge someAnalyticalKnowledge;
	private LegalInformation someLegalInfo;
	private UsageActivity someUsageActivity;
	private UsageActivities someUsageActivities;
	private Bounds someBounds;

	private PortfolioUrlLibrary urlLibrary;
	private HttpRestClient httpClient;

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
	public void setUpAdaptors() {
		urlLibrary = buildUrlLibraryMock();
		httpClient = mock(HttpRestClient.class);
		analyticsAdaptor = new AnalyticsAdaptorImpl();
		analyticsAdaptor.initialize(urlLibrary, httpClient);
		// hbaseModelAdaptor = new HbaseIndexModelAdaptorImpl();
		// hbaseModelAdaptor.initialize(urlLibrary, httpClient);
		legalAdaptor = new LegalAdaptorImpl();
		legalAdaptor.initialize(urlLibrary, httpClient);
		renderingAdaptor = new RenderingAdaptorImpl();
		renderingAdaptor.initialize(urlLibrary, httpClient);
		usageAdaptor = new UsageAdaptorImpl();
		usageAdaptor.initialize(urlLibrary, httpClient);
		adaptors = new Adaptors.Builder().setAnalyticsAdaptor(analyticsAdaptor)
				// .setHbaseModelAdaptor(hbaseModelAdaptor)
				.setLegalAdaptor(legalAdaptor)
				.setRenderingAdaptor(renderingAdaptor)
				.setUsageAdaptor(usageAdaptor)
				.build();
	}

	@Test
	public void shouldGetAnalyticalKnowledge() {
		when(urlLibrary.selectors().knowledge(eq(someEntityId))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(eq(SOME_URL_STRING), eq(GET), any())).thenReturn(new Resource<>(someAnalyticalKnowledge));
		final AnalyticalKnowledge knowledge = adaptors.getKnowledge(someEntityId);
		assertEquals(someAnalyticalKnowledge, knowledge);
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