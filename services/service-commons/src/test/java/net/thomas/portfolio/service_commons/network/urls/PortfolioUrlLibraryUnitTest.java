package net.thomas.portfolio.service_commons.network.urls;

import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static net.thomas.portfolio.enums.HbaseIndexingServiceEndpoint.SCHEMA;
import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_PATH;
import static net.thomas.portfolio.globals.AnalyticsServiceGlobals.LOOKUP_KNOWLEDGE_ROOT_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.DOCUMENTS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.ENTITIES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.FROM_SIMPLE_REP_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.INVERTED_INDEX_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.REFERENCES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SAMPLES_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SELECTORS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.STATISTICS_PATH;
import static net.thomas.portfolio.globals.HbaseIndexingServiceGlobals.SUGGESTIONS_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.AUDIT_LOGGING_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.HISTORY_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_ROOT_PATH;
import static net.thomas.portfolio.globals.LegalServiceGlobals.LEGAL_RULES_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_HTML_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_SIMPLE_REPRESENTATION_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.AS_TEXT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_ENTITY_ROOT_PATH;
import static net.thomas.portfolio.globals.RenderServiceGlobals.RENDER_SELECTOR_ROOT_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.USAGE_ACTIVITIES_PATH;
import static net.thomas.portfolio.globals.UsageDataServiceGlobals.USAGE_ACTIVITIES_ROOT_PATH;
import static net.thomas.portfolio.services.Service.ANALYTICS_SERVICE;
import static net.thomas.portfolio.services.Service.HBASE_INDEXING_SERVICE;
import static net.thomas.portfolio.services.Service.LEGAL_SERVICE;
import static net.thomas.portfolio.services.Service.RENDER_SERVICE;
import static net.thomas.portfolio.services.Service.USAGE_DATA_SERVICE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.globals.LegalServiceGlobals;
import net.thomas.portfolio.services.ContextPathSection;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;
import net.thomas.portfolio.shared_objects.hbase_index.request.InvertedIndexLookupRequest;
import net.thomas.portfolio.shared_objects.legal.LegalInformation;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;

public class PortfolioUrlLibraryUnitTest {
	private static final Integer SOME_INTEGER = 10;
	private static final String SOME_URL_STRING = "SOME_URL";
	private static final String SOME_DATA_TYPE = "DataType";
	private static final String SOME_SIMPLE_REPRESENTATION = "ABCD";
	private static final String SOME_UID = "AA";
	private static final DataTypeId SOME_DATA_TYPE_ID = new DataTypeId(SOME_DATA_TYPE, SOME_UID);
	private static final ParameterGroup SOME_PARAMETER_GROUP = ParameterGroup.asGroup("name", singleton("value"));
	private static final ParameterGroup[] SOME_PARAMETER_GROUPS = { SOME_PARAMETER_GROUP };

	private UsageActivity someUsageActivity;
	private LegalInformation someLegalInfo;
	private Bounds someBounds;
	private InvertedIndexLookupRequest someInvertedIndexLookupRequest;
	private UrlFactory urlFactory;
	private PortfolioUrlLibrary library;

	@Before
	public void setUpUrlGeneration() {
		urlFactory = mock(UrlFactory.class);
		library = new PortfolioUrlLibrary(urlFactory);
	}

	@Before
	public void mockParameterGroups() {
		someUsageActivity = mock(UsageActivity.class);
		when(someUsageActivity.getParameters()).thenReturn(SOME_PARAMETER_GROUP.getParameters());
		someLegalInfo = mock(LegalInformation.class);
		when(someLegalInfo.getParameters()).thenReturn(SOME_PARAMETER_GROUP.getParameters());
		someBounds = mock(Bounds.class);
		when(someBounds.getParameters()).thenReturn(SOME_PARAMETER_GROUP.getParameters());
		someInvertedIndexLookupRequest = mock(InvertedIndexLookupRequest.class);
		when(someInvertedIndexLookupRequest.getSelectorId()).thenReturn(SOME_DATA_TYPE_ID);
		when(someInvertedIndexLookupRequest.getGroups()).thenReturn(SOME_PARAMETER_GROUPS);
	}

	@Test
	public void shouldHaveSchemaUrl() {
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), eq(SCHEMA))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.schema());
	}

	@Test
	public void shouldHaveEntitySamplesUrl() {
		final ContextPathSection endpoint = asContextPath(ENTITIES_PATH, SOME_DATA_TYPE, SAMPLES_PATH);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)), (ParameterGroup) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.entities().samples(SOME_DATA_TYPE, SOME_INTEGER));
	}

	@Test
	public void shouldHaveEntitySamplesParameterAmount() {
		library.entities().samples(SOME_DATA_TYPE, SOME_INTEGER);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_INTEGER)));
	}

	@Test
	public void shouldHaveEntityLookupUrl() {
		final ContextPathSection endpoint = asContextPath(ENTITIES_PATH, SOME_DATA_TYPE, SOME_UID);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.entities().lookup(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveEntityRenderAsTextUrl() {
		final ContextPathSection endpoint = asContextPath(RENDER_ENTITY_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, AS_TEXT_PATH);
		when(urlFactory.buildUrl(eq(RENDER_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.entities().render().text(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveEntityRenderAsHtmlUrl() {
		final ContextPathSection endpoint = asContextPath(RENDER_ENTITY_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, AS_HTML_PATH);
		when(urlFactory.buildUrl(eq(RENDER_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.entities().render().html(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveDocumentSamplesUrl() {
		final ContextPathSection endpoint = asContextPath(DOCUMENTS_PATH, SOME_DATA_TYPE, SAMPLES_PATH);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)), (ParameterGroup) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().samples(SOME_DATA_TYPE, SOME_INTEGER));
	}

	@Test
	public void shouldHaveDocumentSamplesParameterAmount() {
		library.documents().samples(SOME_DATA_TYPE, SOME_INTEGER);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_INTEGER)));
	}

	@Test
	public void shouldHaveDocumentLookupUrl() {
		final ContextPathSection endpoint = asContextPath(DOCUMENTS_PATH, SOME_DATA_TYPE, SOME_UID);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().lookup(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveDocumentReferencesUrl() {
		final ContextPathSection endpoint = asContextPath(DOCUMENTS_PATH, SOME_DATA_TYPE, SOME_UID, REFERENCES_PATH);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().references(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveDocumentGetUsageActivitiesUrl() {
		final ContextPathSection endpoint = asContextPath(USAGE_ACTIVITIES_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, USAGE_ACTIVITIES_PATH);
		when(urlFactory.buildUrl(eq(USAGE_DATA_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().usageActivities(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveDocumentGetUsageActivitiesUrlWithBounds() {
		final ContextPathSection endpoint = asContextPath(USAGE_ACTIVITIES_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, USAGE_ACTIVITIES_PATH);
		when(urlFactory.buildUrl(eq(USAGE_DATA_SERVICE), argThat(matches(endpoint)), (Bounds) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().usageActivities(SOME_DATA_TYPE_ID, someBounds));
	}

	@Test
	public void shouldHaveDocumentPostUsageActivitiesUrl() {
		final ContextPathSection endpoint = asContextPath(USAGE_ACTIVITIES_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, USAGE_ACTIVITIES_PATH);
		when(urlFactory.buildUrl(eq(USAGE_DATA_SERVICE), argThat(matches(endpoint)), (UsageActivity) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().usageActivities(SOME_DATA_TYPE_ID, someUsageActivity));
	}

	@Test
	public void shouldHaveDocumentGetUsageActivitiesParameterBounds() {
		library.documents().usageActivities(SOME_DATA_TYPE_ID, someBounds);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(someBounds)));
	}

	@Test
	public void shouldHaveDocumentPostUsageActivitiesParameterUsageActivity() {
		library.documents().usageActivities(SOME_DATA_TYPE_ID, someUsageActivity);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(someUsageActivity)));
	}

	@Test
	public void shouldHaveDocumentRenderAsTextUrl() {
		final ContextPathSection endpoint = asContextPath(RENDER_ENTITY_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, AS_TEXT_PATH);
		when(urlFactory.buildUrl(eq(RENDER_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().render().text(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveDocumentRenderAsHtmlUrl() {
		final ContextPathSection endpoint = asContextPath(RENDER_ENTITY_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, AS_HTML_PATH);
		when(urlFactory.buildUrl(eq(RENDER_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.documents().render().html(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveSelectorSamplesUrl() {
		final ContextPathSection endpoint = asContextPath(SELECTORS_PATH, SOME_DATA_TYPE, SAMPLES_PATH);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)), (ParameterGroup) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().samples(SOME_DATA_TYPE, SOME_INTEGER));
	}

	@Test
	public void shouldHaveSelectorSamplesParameterAmount() {
		library.selectors().samples(SOME_DATA_TYPE, SOME_INTEGER);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_INTEGER)));
	}

	@Test
	public void shouldHaveSelectorSuggestionsUrl() {
		final ContextPathSection endpoint = asContextPath(SELECTORS_PATH, SUGGESTIONS_PATH, SOME_SIMPLE_REPRESENTATION + "/");
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().suggestions(SOME_SIMPLE_REPRESENTATION));
	}

	@Test
	public void shouldHaveSelectorLookupUrl() {
		final ContextPathSection endpoint = asContextPath(SELECTORS_PATH, SOME_DATA_TYPE, SOME_UID);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().lookup(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveSelectorFromSimpleRepresentationUrl() {
		final ContextPathSection endpoint = asContextPath(SELECTORS_PATH, SOME_DATA_TYPE, FROM_SIMPLE_REP_PATH, SOME_SIMPLE_REPRESENTATION + "/");
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().fromSimpleRepresentation(SOME_DATA_TYPE, SOME_SIMPLE_REPRESENTATION));
	}

	@Test
	public void shouldHaveSelectorInvertedIndexLookupUrl() {
		final ContextPathSection endpoint = asContextPath(SELECTORS_PATH, SOME_DATA_TYPE, SOME_UID, INVERTED_INDEX_PATH);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().invertedIndex(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveSelectorInvertedIndexLookupUrlWithParameter() {
		final ContextPathSection endpoint = asContextPath(SELECTORS_PATH, SOME_DATA_TYPE, SOME_UID, INVERTED_INDEX_PATH);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)), (ParameterGroup[]) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().invertedIndex(someInvertedIndexLookupRequest));
	}

	@Test
	public void shouldHaveSelectorInvertedIndexLookupUrlWithParameterInvertedIndexRequest() {
		library.selectors().invertedIndex(someInvertedIndexLookupRequest);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_PARAMETER_GROUP)));
	}

	@Test
	public void shouldHaveSelectorKnowledgeUrl() {
		final ContextPathSection endpoint = asContextPath(LOOKUP_KNOWLEDGE_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, LOOKUP_KNOWLEDGE_PATH);
		when(urlFactory.buildUrl(eq(ANALYTICS_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().knowledge(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveSelectorStatisticsUrl() {
		final ContextPathSection endpoint = asContextPath(SELECTORS_PATH, SOME_DATA_TYPE, SOME_UID, STATISTICS_PATH);
		when(urlFactory.buildUrl(eq(HBASE_INDEXING_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().statistics(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveSelectorRenderAsTextUrl() {
		final ContextPathSection endpoint = asContextPath(RENDER_ENTITY_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, AS_TEXT_PATH);
		when(urlFactory.buildUrl(eq(RENDER_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().render().text(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveSelectorRenderAsSimpleRepresentationUrl() {
		final ContextPathSection endpoint = asContextPath(RENDER_SELECTOR_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, AS_SIMPLE_REPRESENTATION_PATH);
		when(urlFactory.buildUrl(eq(RENDER_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().render().simpleRepresentation(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveSelectorRenderAsHtmlUrl() {
		final ContextPathSection endpoint = asContextPath(RENDER_ENTITY_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, AS_HTML_PATH);
		when(urlFactory.buildUrl(eq(RENDER_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().render().html(SOME_DATA_TYPE_ID));
	}

	@Test
	public void shouldHaveAuditCheckInvertedIndexUrl() {
		final ContextPathSection endpoint = asContextPath(LEGAL_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, LegalServiceGlobals.INVERTED_INDEX_PATH, LEGAL_RULES_PATH);
		when(urlFactory.buildUrl(eq(LEGAL_SERVICE), argThat(matches(endpoint)), (LegalInformation) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().audit().check().invertedIndex(SOME_DATA_TYPE_ID, someLegalInfo));
	}

	@Test
	public void shouldHaveAuditCheckInvertedIndexUrlWithParameterLegalInfo() {
		library.selectors().audit().check().invertedIndex(SOME_DATA_TYPE_ID, someLegalInfo);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_PARAMETER_GROUP)));
	}

	@Test
	public void shouldHaveAuditCheckStatisticsUrl() {
		final ContextPathSection endpoint = asContextPath(LEGAL_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, LegalServiceGlobals.STATISTICS_PATH, LEGAL_RULES_PATH);
		when(urlFactory.buildUrl(eq(LEGAL_SERVICE), argThat(matches(endpoint)), (LegalInformation) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().audit().check().statistics(SOME_DATA_TYPE_ID, someLegalInfo));
	}

	@Test
	public void shouldHaveAuditCheckStatisticsUrlWithParameterLegalInfo() {
		library.selectors().audit().check().statistics(SOME_DATA_TYPE_ID, someLegalInfo);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_PARAMETER_GROUP)));
	}

	@Test
	public void shouldHaveAuditLogInvertedIndexUrl() {
		final ContextPathSection endpoint = asContextPath(LEGAL_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, LegalServiceGlobals.INVERTED_INDEX_PATH,
				AUDIT_LOGGING_PATH);
		when(urlFactory.buildUrl(eq(LEGAL_SERVICE), argThat(matches(endpoint)), (LegalInformation) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().audit().log().invertedIndex(SOME_DATA_TYPE_ID, someLegalInfo));
	}

	@Test
	public void shouldHaveAuditLogInvertedIndexUrlWithParameterLegalInfo() {
		library.selectors().audit().log().invertedIndex(SOME_DATA_TYPE_ID, someLegalInfo);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_PARAMETER_GROUP)));
	}

	@Test
	public void shouldHaveAuditLogStatisticsUrl() {
		final ContextPathSection endpoint = asContextPath(LEGAL_ROOT_PATH, SOME_DATA_TYPE, SOME_UID, LegalServiceGlobals.STATISTICS_PATH, AUDIT_LOGGING_PATH);
		when(urlFactory.buildUrl(eq(LEGAL_SERVICE), argThat(matches(endpoint)), (LegalInformation) any())).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().audit().log().statistics(SOME_DATA_TYPE_ID, someLegalInfo));
	}

	@Test
	public void shouldHaveAuditLogStatisticsUrlWithParameterLegalInfo() {
		library.selectors().audit().log().statistics(SOME_DATA_TYPE_ID, someLegalInfo);
		verify(urlFactory).buildUrl(any(), any(), argThat(matches(SOME_PARAMETER_GROUP)));
	}

	@Test
	public void shouldHaveLegalHistoryUrl() {
		final ContextPathSection endpoint = asContextPath(LEGAL_ROOT_PATH, HISTORY_PATH);
		when(urlFactory.buildUrl(eq(LEGAL_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().history().all());
	}

	@Test
	public void shouldHaveLegalHistoryForItemUrl() {
		final ContextPathSection endpoint = asContextPath(LEGAL_ROOT_PATH, HISTORY_PATH, "" + SOME_INTEGER + "/");
		when(urlFactory.buildUrl(eq(LEGAL_SERVICE), argThat(matches(endpoint)))).thenReturn(SOME_URL_STRING);
		assertEquals(SOME_URL_STRING, library.selectors().history().item(SOME_INTEGER));
	}

	private ContextPathSection asContextPath(String... elements) {
		return () -> ("/" + stream(elements).collect(joining("/"))).replaceAll("//", "/");
	}

	private ArgumentMatcher<ContextPathSection> matches(ContextPathSection endpoint) {
		return actualEndpoint -> endpoint.getContextPath().equals(actualEndpoint.getContextPath());
	}

	private ArgumentMatcher<ParameterGroup> matches(Integer value) {
		return group -> group.getParameters()[0].getValue().equals(value);
	}

	private ArgumentMatcher<ParameterGroup> matches(ParameterGroup value) {
		return group -> {
			final Parameter[] left = group.getParameters();
			final Parameter[] right = value.getParameters();
			if (left.length != right.length) {
				return false;
			}
			for (int i = 0; i < left.length; i++) {
				if (!(namesAreEqual(left[i], right[i]) && valuesAreEqual(left[i], right[i]))) {
					return false;
				}
			}
			return true;
		};
	}

	private boolean namesAreEqual(Parameter currentLeft, Parameter currentRight) {
		return currentLeft.getName().equals(currentRight.getName());
	}

	private boolean valuesAreEqual(Parameter currentLeft, Parameter currentRight) {
		return currentLeft.getValue().equals(currentRight.getValue());
	}
}
