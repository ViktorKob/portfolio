package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static net.thomas.portfolio.hbase_index.schema.EntitySamplesForTesting.SOME_EMAIL;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

public class EmailVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_EMAIL);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_EMAIL, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnTimeOfEventOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "timeOfEvent", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "timeOfEvent", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "timeOfEvent", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnTimeOfInterceptionOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "timeOfInterception", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "timeOfInterception", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "timeOfInterception", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrentFieldActionOnSubjectOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "subject", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "subject", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "subject", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnMessageOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "message", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "message", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "message", ZERO_TIMES);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnFromOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "from", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "from", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "from", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnToOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "to", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "to", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "to", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnCcOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "cc", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "cc", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "cc", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnBccOnce() {
		visit(SOME_EMAIL);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL, "bcc", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL, "bcc", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL, "bcc", ONCE);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_EMAIL);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_EMAIL, ONCE);
	}
}