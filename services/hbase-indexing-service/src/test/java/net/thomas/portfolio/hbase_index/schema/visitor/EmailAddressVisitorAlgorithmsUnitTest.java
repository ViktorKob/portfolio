package net.thomas.portfolio.hbase_index.schema.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.visitor.utils.InvocationCountingContext;

public class EmailAddressVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_EMAIL_ADDRESS);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_EMAIL_ADDRESS, ONCE);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_EMAIL_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ADDRESS, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ADDRESS, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ADDRESS, "uid", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnDisplayedNameOnce() {
		visit(SOME_EMAIL_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ADDRESS, "localname", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ADDRESS, "localname", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ADDRESS, "localname", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnAddressOnce() {
		visit(SOME_EMAIL_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ADDRESS, "domain", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ADDRESS, "domain", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ADDRESS, "domain", ONCE);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_EMAIL_ADDRESS);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_EMAIL_ADDRESS, ONCE);
	}
}