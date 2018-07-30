package net.thomas.portfolio.hbase_index.schema.visitor;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.visitor.VisitorAlgorithmTester.InvocationCountingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.CachedReflectionBasedEntityVisitorBuilder;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.actions.VisitorGenericFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.cached_reflection.factories.VisitorGenericFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.visitor.naive_reflection.NaiveRelectionBasedEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.visitor.naive_reflection.actions.VisitorNaiveFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.visitor.naive_reflection.actions.VisitorNaiveFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.visitor.naive_reflection.actions.VisitorNaiveFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class EntityVisitorAlgorithmsUnitTest {

	public static List<VisitorTester> algorithms = asList(new NaiveVisitorTester(), new CachingVisitorTester(), new StrictVisitorTester());

	private static final int NEVER = 0;
	private static final int ONCE = 1;
	private static InvocationCountingContext[] countingContexts;

	@BeforeClass
	public static final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
		for (int i = 0; i < countingContexts.length; i++) {
			countingContexts[i] = new InvocationCountingContext();
			final EntityVisitor<InvocationCountingContext> algorithm = algorithms.get(i)
				.getVisitor();
			algorithm.visit(SOME_EMAIL, countingContexts[i]);
		}
	}

	@Test
	public void shouldInvokePreEntityActionOnEmailOnce() {
		assertEqualsForAllAlgorithms(INVOKED_PRE_ENTITY_ACTION_ON, SOME_EMAIL, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnEmailTimeOfEventOnce() {
		assertThatAllAlgorithms(INVOKED_PRE_FIELD_ACTION_ON, SOME_EMAIL, "timeOfEvent", NEVER);
		assertThatAllAlgorithms(INVOKED_SIMPLE_FIELD_ACTION_ON, SOME_EMAIL, "timeOfEvent", ONCE);
		assertThatAllAlgorithms(INVOKED_POST_FIELD_ACTION_ON, SOME_EMAIL, "timeOfEvent", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnEmailTimeOfInterceptionOnce() {
		assertThatAllAlgorithms(INVOKED_PRE_FIELD_ACTION_ON, SOME_EMAIL, "timeOfInterception", NEVER);
		assertThatAllAlgorithms(INVOKED_SIMPLE_FIELD_ACTION_ON, SOME_EMAIL, "timeOfInterception", ONCE);
		assertThatAllAlgorithms(INVOKED_POST_FIELD_ACTION_ON, SOME_EMAIL, "timeOfInterception", NEVER);
	}

	@Test
	public void shouldInvokeCorrentFieldActionOnEmailSubjectOnce() {
		assertThatAllAlgorithms(INVOKED_PRE_FIELD_ACTION_ON, SOME_EMAIL, "subject", NEVER);
		assertThatAllAlgorithms(INVOKED_SIMPLE_FIELD_ACTION_ON, SOME_EMAIL, "subject", ONCE);
		assertThatAllAlgorithms(INVOKED_POST_FIELD_ACTION_ON, SOME_EMAIL, "subject", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnEmailMessageOnce() {
		assertThatAllAlgorithms(INVOKED_PRE_FIELD_ACTION_ON, SOME_EMAIL, "message", NEVER);
		assertThatAllAlgorithms(INVOKED_SIMPLE_FIELD_ACTION_ON, SOME_EMAIL, "message", ONCE);
		assertThatAllAlgorithms(INVOKED_POST_FIELD_ACTION_ON, SOME_EMAIL, "message", NEVER);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnEmailUid() {
		assertThatAllAlgorithms(INVOKED_PRE_FIELD_ACTION_ON, SOME_EMAIL, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_SIMPLE_FIELD_ACTION_ON, SOME_EMAIL, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_POST_FIELD_ACTION_ON, SOME_EMAIL, "uid", NEVER);
	}

	@Test
	public void shouldInvokePostEntityActionOnEmailOnce() {
		assertEqualsForAllAlgorithms(INVOKED_POST_ENTITY_ACTION_ON, SOME_EMAIL, ONCE);
	}

	private void assertEqualsForAllAlgorithms(String action, Entity entity, int occurrances) {
		for (int i = 0; i < algorithms.size(); i++) {
			final int actualCount = countingContexts[i].getEntityActionCount(entity, action);
			final String message = algorithms.get(0)
				.getName() + ": Count for " + action + " was wrong; should have been " + occurrances + ", but was " + actualCount + " for entity "
					+ entity.getClass()
						.getSimpleName();
			assertEquals(message, occurrances, actualCount);
		}
	}

	private void assertThatAllAlgorithms(String action, Entity entity, String field, int occurrances) {
		for (int i = 0; i < algorithms.size(); i++) {
			final int actualCount = countingContexts[i].getEntityActionCount(entity, action);
			final String message = algorithms.get(0)
				.getName() + ": Count for " + action + ", field " + field + " was wrong; should have been " + occurrances + ", but was " + actualCount
					+ " for entity " + entity.getClass()
						.getSimpleName();
			assertEquals(message, occurrances, countingContexts[i].getFieldActionCount(entity, action, field));
		}
	}

	public static final String INVOKED_PRE_ENTITY_ACTION_ON = "preEntityAction";
	public static final String INVOKED_POST_ENTITY_ACTION_ON = "postEntityAction";
	public static final String INVOKED_PRE_FIELD_ACTION_ON = "preFieldAction";
	public static final String INVOKED_POST_FIELD_ACTION_ON = "postFieldAction";
	public static final String INVOKED_SIMPLE_FIELD_ACTION_ON = "simpleFieldAction";

	public static final DisplayedName SOME_DISPLAYED_NAME = new DisplayedName("name");
	public static final Localname SOME_LOCALNAME = new Localname("name");
	public static final Domain SOME_TOP_LEVEL_DOMAIN = new Domain("part");
	public static final Domain SOME_DOMAIN = new Domain("part", SOME_TOP_LEVEL_DOMAIN);
	public static final EmailAddress SOME_EMAIL_ADDRESS = new EmailAddress(SOME_LOCALNAME, SOME_DOMAIN);
	public static final EmailEndpoint SOME_EMAIL_ENDPOINT = new EmailEndpoint(SOME_DISPLAYED_NAME, SOME_EMAIL_ADDRESS);
	public static final Timestamp SOME_TIMESTAMP = new Timestamp(1l);
	public static final String SOME_STRING = "string";
	public static final String SOME_MESSAGE = "message";
	public static final Email SOME_EMAIL = new Email(SOME_TIMESTAMP, SOME_TIMESTAMP, SOME_STRING, SOME_MESSAGE, SOME_EMAIL_ENDPOINT,
			asArray(SOME_EMAIL_ENDPOINT), asArray(SOME_EMAIL_ENDPOINT, SOME_EMAIL_ENDPOINT), asArray(SOME_EMAIL_ENDPOINT));

	static {
		SOME_DISPLAYED_NAME.uid = "00";
		SOME_LOCALNAME.uid = "01";
		SOME_TOP_LEVEL_DOMAIN.uid = "02";
		SOME_DOMAIN.uid = "03";
		SOME_EMAIL_ADDRESS.uid = "04";
		SOME_EMAIL_ENDPOINT.uid = "05";
		SOME_EMAIL.uid = "06";
	}

	private static <T> T[] asArray(@SuppressWarnings("unchecked") T... endpoints) {
		return endpoints;
	}

	static interface VisitorTester {
		String getName();

		EntityVisitor<InvocationCountingContext> getVisitor();
	}

	static class NaiveVisitorTester implements VisitorTester, VisitorEntityPreAction<Entity, InvocationCountingContext>,
			VisitorEntityPostAction<Entity, InvocationCountingContext>, VisitorNaiveFieldPreAction<Entity, InvocationCountingContext>,
			VisitorNaiveFieldSimpleAction<Entity, InvocationCountingContext>, VisitorNaiveFieldPostAction<Entity, InvocationCountingContext> {
		private final EntityVisitor<InvocationCountingContext> visitor;

		public NaiveVisitorTester() {
			visitor = new NaiveRelectionBasedEntityVisitor<>(this, this, this, this, this);
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public EntityVisitor<InvocationCountingContext> getVisitor() {
			return visitor;
		}

		@Override
		public void performEntityPostAction(Entity entity, InvocationCountingContext context) {
			context.addEntityAction(entity, INVOKED_PRE_ENTITY_ACTION_ON);
		}

		@Override
		public void performEntityPreAction(Entity entity, InvocationCountingContext context) {
			context.addEntityAction(entity, INVOKED_POST_ENTITY_ACTION_ON);
		}

		@Override
		public void performNaiveFieldPreAction(Entity entity, InvocationCountingContext context, String field) {
			context.addFieldAction(entity, INVOKED_PRE_FIELD_ACTION_ON, field);
		}

		@Override
		public void performNaiveSimpleFieldAction(Entity entity, InvocationCountingContext context, String field) {
			context.addFieldAction(entity, INVOKED_SIMPLE_FIELD_ACTION_ON, field);
		}

		@Override
		public void performNaiveFieldPostAction(Entity entity, InvocationCountingContext context, String field) {
			context.addFieldAction(entity, INVOKED_POST_FIELD_ACTION_ON, field);
		}
	}

	static class CachingVisitorTester implements VisitorTester, VisitorGenericFieldSimpleActionFactory<InvocationCountingContext>,
			VisitorGenericEntityPreActionFactory<InvocationCountingContext>, VisitorGenericEntityPostActionFactory<InvocationCountingContext>,
			VisitorGenericFieldPreActionFactory<InvocationCountingContext>, VisitorGenericFieldPostActionFactory<InvocationCountingContext> {
		private final EntityVisitor<InvocationCountingContext> visitor;

		public CachingVisitorTester() {
			visitor = new CachedReflectionBasedEntityVisitorBuilder<InvocationCountingContext>(
					new HashSet<>(asList(Email.class, TextMessage.class, Conversation.class))).setEntityPreActionFactory(this)
						.setEntityPostActionFactory(this)
						.setFieldPreActionFactory(this)
						.setFieldSimpleActionFactory(this)
						.setFieldPostActionFactory(this)
						.build();
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public EntityVisitor<InvocationCountingContext> getVisitor() {
			return visitor;
		}

		@Override
		public VisitorGenericEntityPreAction<InvocationCountingContext> getGenericEntityPreAction(Class<? extends Entity> entityClass) {
			return (entity, context) -> {
				context.addEntityAction(entity, INVOKED_PRE_ENTITY_ACTION_ON);
			};
		}

		@Override
		public VisitorGenericEntityPostAction<InvocationCountingContext> getGenericEntityPostAction(Class<? extends Entity> entityClass) {
			return (entity, context) -> {
				context.addEntityAction(entity, INVOKED_POST_ENTITY_ACTION_ON);
			};
		}

		@Override
		public VisitorGenericFieldPostAction<InvocationCountingContext> getGenericFieldPostAction(Class<? extends Entity> entityClass, String field) {
			return (entity, context) -> {
				context.addFieldAction(entity, INVOKED_POST_FIELD_ACTION_ON, field);
			};
		}

		@Override
		public VisitorGenericFieldSimpleAction<InvocationCountingContext> getGenericSimpleFieldAction(Class<? extends Entity> entityClass, String field) {
			return (entity, context) -> {
				context.addFieldAction(entity, INVOKED_SIMPLE_FIELD_ACTION_ON, field);
			};
		}

		@Override
		public VisitorGenericFieldPreAction<InvocationCountingContext> getGenericFieldPreAction(Class<? extends Entity> entityClass, String field) {
			return (entity, context) -> {
				context.addFieldAction(entity, INVOKED_PRE_FIELD_ACTION_ON, field);
			};
		}
	}

	static class StrictVisitorTester implements VisitorTester, VisitorFieldSimpleActionFactory<InvocationCountingContext>,
			VisitorEntityPreActionFactory<InvocationCountingContext>, VisitorEntityPostActionFactory<InvocationCountingContext>,
			VisitorFieldPreActionFactory<InvocationCountingContext>, VisitorFieldPostActionFactory<InvocationCountingContext> {
		private final EntityVisitor<InvocationCountingContext> visitor;

		public StrictVisitorTester() {
			visitor = new StrictEntityHierarchyVisitorBuilder<InvocationCountingContext>().setEntityPreActionFactory(this)
				.setEntityPostActionFactory(this)
				.setFieldPreActionFactory(this)
				.setFieldSimpleActionFactory(this)
				.setFieldPostActionFactory(this)
				.build();
		}

		@Override
		public String getName() {
			return getClass().getSimpleName();
		}

		@Override
		public EntityVisitor<InvocationCountingContext> getVisitor() {
			return visitor;
		}

		@Override
		public <T extends Entity> VisitorEntityPreAction<T, InvocationCountingContext> getEntityPreAction(Class<T> entityClass) {
			return (entity, context) -> {
				context.addEntityAction(entity, INVOKED_PRE_ENTITY_ACTION_ON);
			};
		}

		@Override
		public <T extends Entity> VisitorEntityPostAction<T, InvocationCountingContext> getEntityPostAction(Class<T> entityClass) {
			return (entity, context) -> {
				context.addEntityAction(entity, INVOKED_POST_ENTITY_ACTION_ON);
			};
		}

		@Override
		public <T extends Entity> VisitorFieldSimpleAction<T, InvocationCountingContext> getSimpleFieldAction(Class<T> entityClass, String field) {
			return (entity, context) -> {
				context.addFieldAction(entity, INVOKED_SIMPLE_FIELD_ACTION_ON, field);
			};
		}

		@Override
		public <T extends Entity> VisitorFieldPostAction<T, InvocationCountingContext> getFieldPostAction(Class<T> entityClass, String field) {
			return (entity, context) -> {
				context.addFieldAction(entity, INVOKED_POST_FIELD_ACTION_ON, field);
			};
		}

		@Override
		public <T extends Entity> VisitorFieldPreAction<T, InvocationCountingContext> getFieldPreAction(Class<T> entityClass, String field) {
			return (entity, context) -> {
				context.addFieldAction(entity, INVOKED_PRE_FIELD_ACTION_ON, field);
			};
		}
	}
}