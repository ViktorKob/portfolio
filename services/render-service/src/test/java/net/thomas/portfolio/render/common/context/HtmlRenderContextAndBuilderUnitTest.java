package net.thomas.portfolio.render.common.context;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaImpl;

public class HtmlRenderContextAndBuilderUnitTest {
	private HtmlRenderContextBuilder builder;
	private HbaseIndexSchemaImpl schema;

	@Before
	public void setUpForTest() {
		schema = mock(HbaseIndexSchemaImpl.class);
		builder = new HtmlRenderContextBuilder();
		builder.setSchema(schema);
	}

	@Test
	public void shouldContainSchemaAfterCreation() {
		final HtmlRenderContext context = builder.build();
		assertSame(schema, context.getSchema());
	}
}
