package net.thomas.portfolio.hbase_index.schema.processing;

import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.SOME_LOCALNAME;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import net.thomas.portfolio.hbase_index.schema.processing.EntitySerializerActionFactory.SerializerContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;

public class EntitySerializerActionFactoryUnitTest {
	private JsonGenerator generator;
	private SerializerProvider serializers;
	private SerializerContext context;
	private EntitySerializerActionFactory actionFactory;

	@Before
	public void setUpForTest() {
		generator = mock(JsonGenerator.class);
		serializers = mock(SerializerProvider.class);
		context = new SerializerContext(generator, serializers);
		actionFactory = new EntitySerializerActionFactory();
	}

	@Test
	public void shouldThrowExceptionWhenEntityPreActionNotInitialized() {
		final VisitorEntityPreAction<Localname, SerializerContext> postAction = actionFactory.getEntityPreAction(Localname.class);
		postAction.performEntityPreAction(SOME_LOCALNAME, context);
	}
}
