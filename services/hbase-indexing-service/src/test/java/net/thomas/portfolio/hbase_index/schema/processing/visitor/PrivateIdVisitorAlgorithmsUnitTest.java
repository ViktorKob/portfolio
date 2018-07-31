package net.thomas.portfolio.hbase_index.schema.processing.visitor;

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
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_PRIVATE_ID, "number", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_PRIVATE_ID, "number", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_PRIVATE_ID, "number", NEVER);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_PRIVATE_ID);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_PRIVATE_ID, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_PRIVATE_ID, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_PRIVATE_ID, "uid", NEVER);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_PRIVATE_ID);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_PRIVATE_ID, ONCE);
	}
}