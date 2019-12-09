package net.thomas.portfolio.service_commons.adaptors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

import org.junit.Before;

import net.thomas.portfolio.service_commons.adaptors.impl.AnalyticsAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.HbaseIndexModelAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.LegalAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.RenderingAdaptorImpl;
import net.thomas.portfolio.service_commons.adaptors.impl.UsageAdaptorImpl;
import net.thomas.portfolio.service_commons.network.HttpRestClient;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary;
import net.thomas.portfolio.service_commons.network.urls.PortfolioUrlLibrary.SelectorUrls;
import net.thomas.portfolio.shared_objects.analytics.AnalyticalKnowledge;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;

/***
 * Individual adaptor implementations are tested through the Adaptors class since these should
 * always be in sync
 *
 * NOT IN USE: Pending different implementation of UrlLibrary for Portfolio
 */
public class AdaptorsUnitTest {
	private static final String SOME_URL_STRING = "SomeUrl";

	private DataTypeId someEntityId;
	private AnalyticalKnowledge someAnalyticalKnowledge;

	private PortfolioUrlLibrary urlLibrary;
	private SelectorUrls selectorUrls;

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
	}

	@Before
	public void setUpAdaptors() {
		selectorUrls = mock(SelectorUrls.class);
		urlLibrary = mock(PortfolioUrlLibrary.class);
		httpClient = mock(HttpRestClient.class);
		analyticsAdaptor = new AnalyticsAdaptorImpl();
		analyticsAdaptor.initialize(urlLibrary, httpClient);
		// hbaseModelAdaptor = new HbaseIndexModelAdaptorImpl();
		// hbaseModelAdaptor.initialize(urlLibrary, httpClient);
		// legalAdaptor = new LegalAdaptorImpl();
		// legalAdaptor.initialize(urlLibrary, httpClient);
		// renderingAdaptor = new RenderingAdaptorImpl();
		// renderingAdaptor.initialize(urlLibrary, httpClient);
		// usageAdaptor = new UsageAdaptorImpl();
		// usageAdaptor.initialize(urlLibrary, httpClient);
		adaptors = new Adaptors.Builder().setAnalyticsAdaptor(analyticsAdaptor)
				// .setHbaseModelAdaptor(hbaseModelAdaptor)
				// .setLegalAdaptor(legalAdaptor)
				// .setRenderingAdaptor(renderingAdaptor)
				// .setUsageAdaptor(usageAdaptor)
				.build();
	}

	public void shouldGetAnalyticalKnowledge() {
		when(selectorUrls.knowledge(eq(someEntityId))).thenReturn(SOME_URL_STRING);
		when(httpClient.loadUrlAsObject(SOME_URL_STRING, GET, any())).thenReturn(someAnalyticalKnowledge);
		final AnalyticalKnowledge knowledge = adaptors.getKnowledge(someEntityId);
		assertEquals(someAnalyticalKnowledge, knowledge);
	}
}