package net.thomas.portfolio.hbase_index.schema.processing;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.EntitySerializerActionFactory.SerializerContext;

public class EntitySerializerActionFactoryUnitTest {
	private SerializerContext context;
	private EntitySerializerActionFactory actionFactory;

	@Before
	public void setUpForTest() {
		context = new SerializerContext(null, null);
		actionFactory = new EntitySerializerActionFactory();
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenEntityPreActionNotInitialized() {
		actionFactory.getEntityPreAction(NonExistantEntity.class).performEntityPreAction(SOME_NON_EXISTANT_ENTITY, context);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenEntityPostActionNotInitialized() {
		actionFactory.getEntityPostAction(NonExistantEntity.class).performEntityPostAction(SOME_NON_EXISTANT_ENTITY, context);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenFieldPreActionNotInitialized() {
		actionFactory.getFieldPreAction(NonExistantEntity.class, SOME_FIELD).performFieldPreAction(SOME_NON_EXISTANT_ENTITY, context);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenFieldPostActionNotInitialized() {
		actionFactory.getFieldPostAction(NonExistantEntity.class, SOME_FIELD).performFieldPostAction(SOME_NON_EXISTANT_ENTITY, context);
	}

	@Test(expected = RuntimeException.class)
	public void shouldThrowExceptionWhenFieldSimpleActionNotInitialized() {
		actionFactory.getFieldSimpleAction(NonExistantEntity.class, SOME_FIELD).performFieldSimpleAction(SOME_NON_EXISTANT_ENTITY, context);
	}

	private static final NonExistantEntity SOME_NON_EXISTANT_ENTITY = new NonExistantEntity();
	private static final String SOME_FIELD = "SomeField";

	private static class NonExistantEntity extends Entity {
	}
}
