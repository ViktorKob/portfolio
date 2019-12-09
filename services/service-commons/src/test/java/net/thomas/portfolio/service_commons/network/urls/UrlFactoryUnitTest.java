package net.thomas.portfolio.service_commons.network.urls;

import static net.thomas.portfolio.services.Service.ADMIN_SERVICE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.ServiceDependency;
import net.thomas.portfolio.services.ContextPathSection;
import net.thomas.portfolio.services.Service;

public class UrlFactoryUnitTest {
	private static final String SOME_PREFIX = "SomePrefix";
	private static final String SOME_SUFFIX = "SomeSuffix";
	private static final Service SOME_SERVICE = ADMIN_SERVICE;
	private ContextPathSection someResourcePath;
	private Parameter someParameter;
	private ParameterGroup someParameterGroup;
	private UrlPrefixBuilder prefixBuilder;
	private UrlSuffixBuilder suffixBuilder;
	private UrlFactory factory;

	@Before
	public void setUp() {
		someResourcePath = mock(ContextPathSection.class);
		someParameter = mock(Parameter.class);
		someParameterGroup = mock(ParameterGroup.class);
		prefixBuilder = mock(UrlPrefixBuilder.class);
		when(prefixBuilder.build()).thenReturn("");
		suffixBuilder = mock(UrlSuffixBuilder.class);
		when(suffixBuilder.buildUrlSuffix(eq(SOME_SERVICE), eq(someResourcePath))).thenReturn("");
		when(suffixBuilder.buildUrlSuffix(eq(SOME_SERVICE), eq(someResourcePath), eq(someParameter))).thenReturn("");
		when(suffixBuilder.buildUrlSuffix(eq(SOME_SERVICE), eq(someResourcePath), eq(someParameterGroup))).thenReturn("");
		factory = new UrlFactory(prefixBuilder, suffixBuilder);
	}

	@Test
	public void shouldIncludePrefixInUrlWhenBuildingSimpleVariant() {
		when(prefixBuilder.build()).thenReturn(SOME_PREFIX);
		final String url = factory.buildUrl(SOME_SERVICE, someResourcePath);
		assertEquals(SOME_PREFIX, url);
	}

	@Test
	public void shouldIncludePrefixInUrlWhenBuildingParameterVariant() {
		when(prefixBuilder.build()).thenReturn(SOME_PREFIX);
		final String url = factory.buildUrl(SOME_SERVICE, someResourcePath, someParameter);
		assertEquals(SOME_PREFIX, url);
	}

	@Test
	public void shouldIncludePrefixInUrlWhenBuildingParameterGroupVariant() {
		when(prefixBuilder.build()).thenReturn(SOME_PREFIX);
		final String url = factory.buildUrl(SOME_SERVICE, someResourcePath, someParameterGroup);
		assertEquals(SOME_PREFIX, url);
	}

	@Test
	public void shouldIncludeSuffixInUrlWhenBuildingSimpleVariant() {
		when(suffixBuilder.buildUrlSuffix(eq(SOME_SERVICE), eq(someResourcePath))).thenReturn(SOME_SUFFIX);
		final String url = factory.buildUrl(SOME_SERVICE, someResourcePath);
		assertEquals(SOME_SUFFIX, url);
	}

	@Test
	public void shouldIncludeSuffixInUrlWhenBuildingParameterVariant() {
		when(suffixBuilder.buildUrlSuffix(eq(SOME_SERVICE), eq(someResourcePath), eq(someParameter))).thenReturn(SOME_SUFFIX);
		final String url = factory.buildUrl(SOME_SERVICE, someResourcePath, someParameter);
		assertEquals(SOME_SUFFIX, url);
	}

	@Test
	public void shouldIncludeSuffixInUrlWhenBuildingParameterGroupVariant() {
		when(suffixBuilder.buildUrlSuffix(eq(SOME_SERVICE), eq(someResourcePath), eq(someParameterGroup))).thenReturn(SOME_SUFFIX);
		final String url = factory.buildUrl(SOME_SERVICE, someResourcePath, someParameterGroup);
		assertEquals(SOME_SUFFIX, url);
	}

	@Test
	public void shouldBuildPortfolioUrlFactory() {
		final EurekaClient discoveryClient = mock(EurekaClient.class);
		final ServiceDependency serviceInfo = mock(ServiceDependency.class);
		final UrlFactory factory = UrlFactory.usingPortfolio(discoveryClient, serviceInfo);
		assertNotNull(factory);
	}
}
