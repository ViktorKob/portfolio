package net.thomas.portfolio.hbase_index.schema;

import static java.lang.System.nanoTime;
import static java.util.stream.Collectors.averagingLong;
import static java.util.stream.Collectors.joining;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.thomas.portfolio.hbase_index.fake.FakeWorld;
import net.thomas.portfolio.hbase_index.schema.SchemaTraversalPerformanceTester.TestContext;
import net.thomas.portfolio.hbase_index.schema.documents.Event;
import net.thomas.portfolio.hbase_index.schema.visitor.EntityVisitor;
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
import net.thomas.portfolio.hbase_index.schema.visitor.strict_implementation.StrictEntityHierarchyVisitorBuilder;

public class SchemaTraversalPerformanceTester implements VisitorFieldSimpleActionFactory<TestContext>, VisitorEntityPreActionFactory<TestContext>,
		VisitorEntityPostActionFactory<TestContext>, VisitorFieldPreActionFactory<TestContext>, VisitorFieldPostActionFactory<TestContext>,
		VisitorFieldSimpleAction<Entity, TestContext>, VisitorEntityPreAction<Entity, TestContext>, VisitorEntityPostAction<Entity, TestContext>,
		VisitorFieldPreAction<Entity, TestContext>, VisitorFieldPostAction<Entity, TestContext> {
	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		final int iterations = 10;
		final SchemaTraversalPerformanceTester tester = new SchemaTraversalPerformanceTester();
		final EntityVisitor<TestContext> strictVisitor = buildManuallyImplementedAlgorithm(tester);
		final EntityVisitor<TestContext> reflectionVisitor = buildNaiveReflectionBasedAlgorithm(tester);
		final TestCase[] testCases = buildTestCases(strictVisitor, reflectionVisitor);
		final FakeWorld world = new FakeWorld(1234l, 200, 15, 2000);
		runTestCases(world, iterations, testCases);
		printResults(testCases);
		printCountsWereIdentical(testCases);
	}

	private static EntityVisitor<TestContext> buildManuallyImplementedAlgorithm(final SchemaTraversalPerformanceTester tester) {
		final EntityVisitor<TestContext> strictVisitor = new StrictEntityHierarchyVisitorBuilder<TestContext>().setEntityPreActionFactory(tester)
			.setEntityPostActionFactory(tester)
			.setFieldPreActionFactory(tester)
			.setFieldSimpleActionFactory(tester)
			.setFieldPostActionFactory(tester)
			.build();
		return strictVisitor;
	}

	private static EntityVisitor<TestContext> buildNaiveReflectionBasedAlgorithm(final SchemaTraversalPerformanceTester tester) {
		final EntityVisitor<TestContext> reflectionVisitor = new NaiveRelectionBasedEntityVisitor<>(tester, tester, tester, tester, tester);
		return reflectionVisitor;
	}

	private static TestCase[] buildTestCases(final EntityVisitor<TestContext> strictVisitor, final EntityVisitor<TestContext> reflectionVisitor) {
		return new TestCase[] { new TestCase("Direct implementation", strictVisitor), new TestCase("Naive reflection based", reflectionVisitor) };
	}

	private static void runTestCases(final FakeWorld world, final int iterations, final TestCase[] testCases) {
		for (int i = 0; i < iterations; i++) {
			for (int j = 0; j < testCases.length; j++) {
				testCases[j].executeOn(world.getEvents());
			}
		}
	}

	private static void printResults(final TestCase[] testCases) {
		for (int j = 0; j < testCases.length; j++) {
			testCases[j].printResults();
		}
	}

	private static void printCountsWereIdentical(TestCase[] testCases) {
		boolean identical = true;
		final TestContext firstCounts = testCases[0].context;
		for (int i = 1; i < testCases.length; i++) {
			final TestContext counts = testCases[i].context;
			identical &= firstCounts.entityPostInvocationCount == counts.entityPostInvocationCount;
			identical &= firstCounts.entityPreInvocationCount == counts.entityPreInvocationCount;
			identical &= firstCounts.fieldPostInvocationCount == counts.fieldPostInvocationCount;
			identical &= firstCounts.fieldPreInvocationCount == counts.fieldPreInvocationCount;
			identical &= firstCounts.fieldSimpleInvocationCount == counts.fieldSimpleInvocationCount;
		}
		if (identical) {
			System.out.println("All counts were identical");
		} else {
			System.out.println("Some counts were different:");
			for (int i = 0; i < testCases.length; i++) {
				System.out.println(testCases[i].context);
			}
		}
	}

	static class TestCase {
		private final String name;
		private final EntityVisitor<TestContext> visitor;
		public TestContext context;
		private final List<Long> results;

		public TestCase(String name, EntityVisitor<TestContext> visitor) {
			this.name = name;
			this.visitor = visitor;
			context = new TestContext();
			results = new LinkedList<>();
		}

		public void executeOn(Collection<Event> events) {
			final long stamp = nanoTime();
			for (final Event entity : events) {
				visitor.visit(entity, context);
			}
			results.add(nanoTime() - stamp);
		}

		public void printResults() {
			System.out.println("Time spend on " + name + " (" + results.size() + " runs): [" + results.stream()
				.map(time -> time / 1000000.0)
				.map(String::valueOf)
				.collect(joining(", ")) + "] millis, average: "
					+ results.stream()
						.collect(averagingLong(Long::longValue)) / 1000000.0
					+ " millis.");
		}
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