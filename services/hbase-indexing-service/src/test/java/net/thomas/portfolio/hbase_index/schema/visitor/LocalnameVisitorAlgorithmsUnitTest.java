package net.thomas.portfolio.hbase_index.schema.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.visitor.utils.InvocationCountingContext;

public class LocalnameVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_LOCALNAME);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_LOCALNAME, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnLocalnameOnce() {
		visit(SOME_LOCALNAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_LOCALNAME, "name", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_LOCALNAME, "name", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_LOCALNAME, "name", NEVER);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_LOCALNAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_LOCALNAME, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_LOCALNAME, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_LOCALNAME, "uid", NEVER);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_LOCALNAME);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_LOCALNAME, ONCE);
	}
}