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
import net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.NaiveRelectionBasedEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.actions.VisitorNaiveFieldPostAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.actions.VisitorNaiveFieldPreAction;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.naive_reflection.actions.VisitorNaiveFieldSimpleAction;

public class NaiveVisitorTester implements VisitorTester, VisitorEntityPreAction<Entity, InvocationCountingContext>,
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
	public void performEntityPreAction(Entity entity, InvocationCountingContext context) {
		context.addEntityAction(entity, INVOKED_ENTITY_PRE_ACTION_ON);
	}

	@Override
	public void performEntityPostAction(Entity entity, InvocationCountingContext context) {
		context.addEntityAction(entity, INVOKED_ENTITY_POST_ACTION_ON);
	}

	@Override
	public void performNaiveFieldPreAction(Entity entity, InvocationCountingContext context, String field) {
		context.addFieldAction(entity, INVOKED_FIELD_PRE_ACTION_ON, field);
	}

	@Override
	public void performNaiveSimpleFieldAction(Entity entity, InvocationCountingContext context, String field) {
		context.addFieldAction(entity, INVOKED_FIELD_SIMPLE_ACTION_ON, field);
	}

	@Override
	public void performNaiveFieldPostAction(Entity entity, InvocationCountingContext context, String field) {
		context.addFieldAction(entity, INVOKED_FIELD_POST_ACTION_ON, field);
	}
}