package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

public class TextMessageVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_TEXT_MESSAGE, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnTimeOfEventOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "timeOfEvent", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "timeOfEvent", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "timeOfEvent", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnTimeOfInterceptionOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "timeOfInterception", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "timeOfInterception", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "timeOfInterception", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnMessageOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "message", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "message", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "message", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnSenderLocationOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "senderLocation", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "senderLocation", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "senderLocation", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnReceiverLocationOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "receiverLocation", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "receiverLocation", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "receiverLocation", NEVER);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "uid", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnSenderOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "sender", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "sender", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "sender", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnReceiverOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_TEXT_MESSAGE, "receiver", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_TEXT_MESSAGE, "receiver", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_TEXT_MESSAGE, "receiver", ONCE);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_TEXT_MESSAGE);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_TEXT_MESSAGE, ONCE);
	}
}