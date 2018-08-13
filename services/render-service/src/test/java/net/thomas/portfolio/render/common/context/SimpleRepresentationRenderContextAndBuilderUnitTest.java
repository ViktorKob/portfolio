package net.thomas.portfolio.render.common.context;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.service_commons.adaptors.specific.HbaseIndexModelAdaptor;

public class SimpleRepresentationRenderContextAndBuilderUnitTest {
	private SimpleRepresentationRenderContextBuilder builder;
	private HbaseIndexModelAdaptor adaptor;

	@Before
	public void setUpForTest() {
		adaptor = mock(HbaseIndexModelAdaptor.class);
		builder = new SimpleRepresentationRenderContextBuilder();
		builder.setHbaseModelAdaptor(adaptor);
	}

	@Test
	public void shouldContainSchemaAfterCreation() {
		final SimpleRepresentationRenderContext context = builder.build();
		assertSame(adaptor, context.getHbaseModelAdaptor());
	}
}
