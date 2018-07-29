package net.thomas.portfolio.hbase_index.schema;

import static java.lang.System.nanoTime;

import net.thomas.portfolio.hbase_index.fake.FakeWorld;
import net.thomas.portfolio.hbase_index.schema.SchemaTraversalPerformanceTester.TestContext;
import net.thomas.portfolio.hbase_index.schema.documents.Event;
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
import net.thomas.portfolio.hbase_index.schema.visitor.contexts.VisitingContext;
import net.thomas.portfolio.hbase_index.schema.visitor.naive_reflection.NaiveRelectionBasedEntityVisitor;
import net.thomas.portfolio.hbase_index.schema.visitor.strict_implementation.StrictEntityHierarchyVisitor;
import net.thomas.portfolio.hbase_index.schema.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;

public class SchemaTraversalPerformanceTester implements VisitorFieldSimpleActionFactory<TestContext>, VisitorEntityPreActionFactory<TestContext>,
		VisitorEntityPostActionFactory<TestContext>, VisitorFieldPreActionFactory<TestContext>, VisitorFieldPostActionFactory<TestContext>,
		VisitorFieldSimpleAction<Entity, TestContext>, VisitorEntityPreAction<Entity, TestContext>, VisitorEntityPostAction<Entity, TestContext>,
		VisitorFieldPreAction<Entity, TestContext>, VisitorFieldPostAction<Entity, TestContext> {
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		final SchemaTraversalPerformanceTester tester = new SchemaTraversalPerformanceTester();
		final StrictEntityHierarchyVisitor<TestContext> visitor = new StrictEntityHierarchyVisitorBuilder<TestContext>().setEntityPreActionFactory(tester)
			.setEntityPostActionFactory(tester)
			.setFieldPreActionFactory(tester)
			.setFieldSimpleActionFactory(tester)
			.setFieldPostActionFactory(tester)
			.build();
		final FakeWorld world = new FakeWorld(1234l, 100, 10, 1000);
		TestContext context = new TestContext();
		long stamp = nanoTime();
		for (final Event entity : world.getEvents()) {
			visitor.visit(entity, context);
		}
		System.out.println("Time spend using precoded hierarchy visitor on " + world.getEvents()
			.size() + " events: " + (nanoTime() - stamp) / 1000000.0 + " millis.\n(Counts were " + context.toString() + ").");
		final NaiveRelectionBasedEntityVisitor<TestContext> reflectionVisitor = new NaiveRelectionBasedEntityVisitor<>(tester, tester, tester, tester, tester);
		context = new TestContext();
		stamp = nanoTime();
		for (final Event entity : world.getEvents()) {
			reflectionVisitor.visit(entity, context);
		}
		System.out.println("Time spend using naive reflection based hierarchy visitor on " + world.getEvents()
			.size() + " events: " + (nanoTime() - stamp) / 1000000.0 + " millis.\n(Counts were " + context.toString() + ").");
	}

	static class TestContext implements VisitingContext {
		public int entityPreInvocationCount;
		public int entityPostInvocationCount;
		public int fieldPreInvocationCount;
		public int fieldPostInvocationCount;
		public int fieldSimpleInvocationCount;

		public TestContext() {
			entityPreInvocationCount = 0;
			entityPostInvocationCount = 0;
			fieldPreInvocationCount = 0;
			fieldPostInvocationCount = 0;
			fieldSimpleInvocationCount = 0;
		}

		@Override
		public String toString() {
			return "TestContext [entityPreInvocationCount=" + entityPreInvocationCount + ", entityPostInvocationCount=" + entityPostInvocationCount
					+ ", fieldPreInvocationCount=" + fieldPreInvocationCount + ", fieldPostInvocationCount=" + fieldPostInvocationCount
					+ ", fieldSimpleInvocationCount=" + fieldSimpleInvocationCount + "]";
		}
	}

	@Override
	public <T extends Entity> VisitorEntityPreAction<T, TestContext> getEntityPreAction(Class<T> entityClass) {
		return this::performEntityPreAction;
	}

	@Override
	public <T extends Entity> VisitorEntityPostAction<T, TestContext> getEntityPostAction(Class<T> entityClass) {
		return this::performEntityPostAction;
	}

	@Override
	public <T extends Entity> VisitorFieldPreAction<T, TestContext> getFieldPreAction(Class<T> entityClass, String field) {
		return this::performFieldPreAction;
	}

	@Override
	public <T extends Entity> VisitorFieldSimpleAction<T, TestContext> getSimpleFieldAction(Class<T> entityClass, String field) {
		return this::performSimpleFieldAction;
	}

	@Override
	public <T extends Entity> VisitorFieldPostAction<T, TestContext> getFieldPostAction(Class<T> entityClass, String field) {
		return this::performFieldPostAction;
	}

	@Override
	public void performEntityPreAction(Entity entity, TestContext context) {
		context.entityPreInvocationCount++;
	}

	@Override
	public void performEntityPostAction(Entity entity, TestContext context) {
		context.entityPostInvocationCount++;
	}

	@Override
	public void performFieldPreAction(Entity entity, TestContext context) {
		context.fieldPreInvocationCount++;
	}

	@Override
	public void performFieldPostAction(Entity entity, TestContext context) {
		context.fieldPostInvocationCount++;
	}

	@Override
	public void performSimpleFieldAction(Entity entity, TestContext context) {
		context.fieldSimpleInvocationCount++;
	}
}