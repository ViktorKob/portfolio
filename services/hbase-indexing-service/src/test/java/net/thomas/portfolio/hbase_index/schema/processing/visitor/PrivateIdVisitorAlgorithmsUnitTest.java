package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.SOME_PRIVATE_ID;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

public class PrivateIdVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_PRIVATE_ID);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_PRIVATE_ID, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnNumberOnce() {
		visit(SOME_PRIVATE_ID);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_PRIVATE_ID, "number", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_PRIVATE_ID, "number", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_PRIVATE_ID, "number", ZERO_TIMES);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_PRIVATE_ID);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_PRIVATE_ID, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_PRIVATE_ID, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_PRIVATE_ID, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_PRIVATE_ID);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_PRIVATE_ID, ONCE);
	}
}