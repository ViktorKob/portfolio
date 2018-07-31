package net.thomas.portfolio.hbase_index.schema.processing.visitor.utils;

import static java.util.Arrays.asList;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_ENTITY_POST_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_ENTITY_PRE_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_FIELD_POST_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_FIELD_PRE_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_FIELD_SIMPLE_ACTION_ON;

import java.util.HashSet;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.CachedReflectionBasedEntityVisitorBuilder;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.actions.VisitorGenericFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.cached_reflection.factories.VisitorGenericFieldSimpleActionFactory;

public class CachingVisitorTester implements VisitorTester, VisitorGenericFieldSimpleActionFactory<InvocationCountingContext>,
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
			context.addEntityAction(entity, INVOKED_ENTITY_PRE_ACTION_ON);
		};
	}

	@Override
	public VisitorGenericEntityPostAction<InvocationCountingContext> getGenericEntityPostAction(Class<? extends Entity> entityClass) {
		return (entity, context) -> {
			context.addEntityAction(entity, INVOKED_ENTITY_POST_ACTION_ON);
		};
	}

	@Override
	public VisitorGenericFieldPreAction<InvocationCountingContext> getGenericFieldPreAction(Class<? extends Entity> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, INVOKED_FIELD_PRE_ACTION_ON, field);
		};
	}

	@Override
	public VisitorGenericFieldSimpleAction<InvocationCountingContext> getGenericSimpleFieldAction(Class<? extends Entity> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, INVOKED_FIELD_SIMPLE_ACTION_ON, field);
		};
	}

	@Override
	public VisitorGenericFieldPostAction<InvocationCountingContext> getGenericFieldPostAction(Class<? extends Entity> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, INVOKED_FIELD_POST_ACTION_ON, field);
		};
	}
}