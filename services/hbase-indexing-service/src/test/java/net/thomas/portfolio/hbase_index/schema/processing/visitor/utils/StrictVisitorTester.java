package net.thomas.portfolio.hbase_index.schema.processing.visitor.utils;

import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_ENTITY_POST_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_ENTITY_PRE_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_FIELD_POST_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_FIELD_PRE_ACTION_ON;
import static net.thomas.portfolio.hbase_index.schema.processing.visitor.VisitorAlgorithmUnitTest.INVOKED_FIELD_SIMPLE_ACTION_ON;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.EntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorEntityPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.VisitorFieldSimpleAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorEntityPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPostActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldPreActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.actions.factories.VisitorFieldSimpleActionFactory;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;

public class StrictVisitorTester implements VisitorTester, VisitorFieldSimpleActionFactory<InvocationCountingContext>,
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
			context.addEntityAction(entity, INVOKED_ENTITY_PRE_ACTION_ON);
		};
	}

	@Override
	public <T extends Entity> VisitorEntityPostAction<T, InvocationCountingContext> getEntityPostAction(Class<T> entityClass) {
		return (entity, context) -> {
			context.addEntityAction(entity, INVOKED_ENTITY_POST_ACTION_ON);
		};
	}

	@Override
	public <T extends Entity> VisitorFieldPreAction<T, InvocationCountingContext> getFieldPreAction(Class<T> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, INVOKED_FIELD_PRE_ACTION_ON, field);
		};
	}

	@Override
	public <T extends Entity> VisitorFieldSimpleAction<T, InvocationCountingContext> getSimpleFieldAction(Class<T> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, INVOKED_FIELD_SIMPLE_ACTION_ON, field);
		};
	}

	@Override
	public <T extends Entity> VisitorFieldPostAction<T, InvocationCountingContext> getFieldPostAction(Class<T> entityClass, String field) {
		return (entity, context) -> {
			context.addFieldAction(entity, INVOKED_FIELD_POST_ACTION_ON, field);
		};
	}
}