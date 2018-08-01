package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.util.List;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.CachingVisitorTester;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.NaiveVisitorTester;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.StrictVisitorTester;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.VisitorTester;

public class VisitorAlgorithmUnitTest {

	protected static final int ZERO_TIMES = 0;
	protected static final int ONCE = 1;
	protected static final List<VisitorTester> algorithms = asList(new NaiveVisitorTester(), new CachingVisitorTester(), new StrictVisitorTester());
	protected static InvocationCountingContext[] countingContexts;

	protected void visit(final Entity entity) {
		for (int i = 0; i < countingContexts.length; i++) {
			countingContexts[i] = new InvocationCountingContext();
			final EntityVisitor<InvocationCountingContext> algorithm = algorithms.get(i)
				.getVisitor();
			algorithm.visit(entity, countingContexts[i]);
		}
	}

	protected void assertEqualsForAllAlgorithms(String action, Entity entity, int occurrances) {
		for (int i = 0; i < algorithms.size(); i++) {
			final int actualCount = countingContexts[i].getEntityActionCount(entity, action);
			final String message = algorithms.get(0)
				.getName() + ": Count for " + action + " was wrong; should have been " + occurrances + ", but was " + actualCount + " for entity "
					+ entity.getClass()
						.getSimpleName();
			assertEquals(message, occurrances, actualCount);
		}
	}

	protected void assertThatAllAlgorithms(String action, Entity entity, String field, int occurrances) {
		for (int i = 0; i < algorithms.size(); i++) {
			final int actualCount = countingContexts[i].getFieldActionCount(entity, action, field);
			final String message = algorithms.get(0)
				.getName() + ": Count for " + action + ", field " + field + " was wrong; should have been " + occurrances + ", but was " + actualCount
					+ " for entity " + entity.getClass()
						.getSimpleName();
			assertEquals(message, occurrances, actualCount);
		}
	}

	public static final String INVOKED_ENTITY_PRE_ACTION_ON = "preEntityAction";
	public static final String INVOKED_ENTITY_POST_ACTION_ON = "postEntityAction";
	public static final String INVOKED_FIELD_PRE_ACTION_ON = "preFieldAction";
	public static final String INVOKED_FIELD_POST_ACTION_ON = "postFieldAction";
	public static final String INVOKED_FIELD_SIMPLE_ACTION_ON = "simpleFieldAction";

}
