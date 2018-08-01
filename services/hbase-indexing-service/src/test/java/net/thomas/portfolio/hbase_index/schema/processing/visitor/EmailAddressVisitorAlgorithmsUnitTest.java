package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.SOME_EMAIL_ADDRESS;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

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
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ADDRESS, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ADDRESS, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ADDRESS, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnDisplayedNameOnce() {
		visit(SOME_EMAIL_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ADDRESS, "localname", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ADDRESS, "localname", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ADDRESS, "localname", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnAddressOnce() {
		visit(SOME_EMAIL_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ADDRESS, "domain", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ADDRESS, "domain", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ADDRESS, "domain", ONCE);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_EMAIL_ADDRESS);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_EMAIL_ADDRESS, ONCE);
	}
}