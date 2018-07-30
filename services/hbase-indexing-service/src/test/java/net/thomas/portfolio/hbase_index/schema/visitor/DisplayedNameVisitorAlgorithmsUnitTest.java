package net.thomas.portfolio.hbase_index.schema.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.visitor.utils.InvocationCountingContext;

public class DisplayedNameVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_DISPLAYED_NAME);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_DISPLAYED_NAME, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnLocalnameOnce() {
		visit(SOME_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DISPLAYED_NAME, "name", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DISPLAYED_NAME, "name", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DISPLAYED_NAME, "name", NEVER);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DISPLAYED_NAME, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DISPLAYED_NAME, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DISPLAYED_NAME, "uid", NEVER);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_DISPLAYED_NAME);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_DISPLAYED_NAME, ONCE);
	}
}