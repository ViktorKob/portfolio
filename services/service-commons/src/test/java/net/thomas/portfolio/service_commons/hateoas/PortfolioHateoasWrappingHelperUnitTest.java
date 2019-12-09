package net.thomas.portfolio.service_commons.hateoas;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

public class PortfolioHateoasWrappingHelperUnitTest {

	private static final String SOME_VALUE = "value";

	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void shouldUnwrapResource() {
		final Resource<String> resource = mock(StringResource.class);
		when(resource.getContent()).thenReturn(SOME_VALUE);
		final String value = PortfolioHateoasWrappingHelper.unwrap(resource);
		assertEquals(SOME_VALUE, value);
	}

	@Test
	public void shouldIgnoreNullResource() {
		final String value = PortfolioHateoasWrappingHelper.unwrap((StringResource) null);
		assertNull(value);
	}

	@Test
	public void shouldUnwrapResources() {
		final Resources<String> resource = mock(StringResources.class);
		when(resource.getContent()).thenReturn(singleton(SOME_VALUE));
		final List<String> value = PortfolioHateoasWrappingHelper.unwrap(resource);
		assertEquals(singletonList(SOME_VALUE), value);
	}

	@Test
	public void shouldIgnoreNullResources() {
		final List<String> values = PortfolioHateoasWrappingHelper.unwrap((StringResources) null);
		assertNull(values);
	}

	private static class StringResource extends Resource<String> {
		StringResource() {
			super(null);
		}
	}

	private static class StringResources extends Resources<String> {
		StringResources() {
			super(null);
		}
	}
}
