package net.thomas.portfolio.service_commons.network;

import static net.thomas.portfolio.common.services.parameters.ParameterGroup.asGroup;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.SingleParameter;
import net.thomas.portfolio.services.ContextPathSection;

public class UrlSuffixBuilderImplUnitTest {
	private static final String SOME_SERVICE_PATH = "/SomeEndpointPath";
	private static final String SOME_RESOURCE_PATH = "/SomeResourcePath";
	private static final String SOME_PARAMETER_NAME = "SOME_PARAMETER_NAME";
	private static final String SOME_PARAMETER_VALUE = "SOME_PARAMETER_VALUE";
	private UrlSuffixBuilderImpl builder;
	private ContextPathSection servicePath;
	private ContextPathSection resourcePath;

	@Before
	public void setUp() {
		builder = new UrlSuffixBuilderImpl();
		servicePath = mock(ContextPathSection.class);
		resourcePath = mock(ContextPathSection.class);
		when(servicePath.getContextPath()).thenReturn(SOME_SERVICE_PATH);
		when(resourcePath.getContextPath()).thenReturn(SOME_RESOURCE_PATH);
	}

	@Test
	public void shouldBuildBasicUrl() {
		final String urlSuffix = builder.buildUrlSuffix(servicePath, resourcePath);
		assertEquals(SOME_SERVICE_PATH + SOME_RESOURCE_PATH, urlSuffix);
	}

	@Test
	public void shouldBuildUrlWithSingleParameter() {
		final String actualSuffix = builder.buildUrlSuffix(servicePath, resourcePath, new SingleParameter(SOME_PARAMETER_NAME, SOME_PARAMETER_VALUE));
		final String expectedSuffix = SOME_SERVICE_PATH + SOME_RESOURCE_PATH + "?" + SOME_PARAMETER_NAME + "=" + SOME_PARAMETER_VALUE;
		assertEquals(expectedSuffix, actualSuffix);
	}

	@Test
	public void shouldBuildUrlWithMultipleParameters() {
		final String actualSuffix = builder.buildUrlSuffix(servicePath, resourcePath, new SingleParameter(SOME_PARAMETER_NAME, SOME_PARAMETER_VALUE),
				new SingleParameter(SOME_PARAMETER_NAME, SOME_PARAMETER_VALUE));
		final String expectedSuffix = SOME_SERVICE_PATH + SOME_RESOURCE_PATH + "?" + SOME_PARAMETER_NAME + "=" + SOME_PARAMETER_VALUE + "&"
				+ SOME_PARAMETER_NAME + "=" + SOME_PARAMETER_VALUE;
		assertEquals(expectedSuffix, actualSuffix);
	}

	@Test
	public void shouldBuildUrlWithParameterGroup() {
		final ParameterGroup parameterGroup = asGroup(new SingleParameter(SOME_PARAMETER_NAME, SOME_PARAMETER_VALUE),
				new SingleParameter(SOME_PARAMETER_NAME, SOME_PARAMETER_VALUE));
		final String actualSuffix = builder.buildUrlSuffix(servicePath, resourcePath, parameterGroup);
		final String expectedSuffix = SOME_SERVICE_PATH + SOME_RESOURCE_PATH + "?" + SOME_PARAMETER_NAME + "=" + SOME_PARAMETER_VALUE + "&"
				+ SOME_PARAMETER_NAME + "=" + SOME_PARAMETER_VALUE;
		assertEquals(expectedSuffix, actualSuffix);
	}

	@Test
	public void shouldIgnoreNullParameter() {
		final String urlSuffix = builder.buildUrlSuffix(servicePath, resourcePath, (Parameter) null);
		assertEquals(SOME_SERVICE_PATH + SOME_RESOURCE_PATH, urlSuffix);
	}

	@Test
	public void shouldIgnoreParameterWithNullValue() {
		final String urlSuffix = builder.buildUrlSuffix(servicePath, resourcePath, new SingleParameter(SOME_PARAMETER_NAME, null));
		assertEquals(SOME_SERVICE_PATH + SOME_RESOURCE_PATH, urlSuffix);
	}
}
