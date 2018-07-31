package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

public class ConversationVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_CONVERSATION);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_CONVERSATION, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnTimeOfEventOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "timeOfEvent", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "timeOfEvent", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "timeOfEvent", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnTimeOfInterceptionOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "timeOfInterception", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "timeOfInterception", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "timeOfInterception", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnDurationInSecondsOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "durationInSeconds", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "durationInSeconds", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "durationInSeconds", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPrimaryLocationOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "primaryLocation", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "primaryLocation", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "primaryLocation", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnSecondaryLocationOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "secondaryLocation", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "secondaryLocation", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "secondaryLocation", NEVER);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "uid", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPrimaryOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "primary", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "primary", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "primary", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnSecondaryOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "secondary", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "secondary", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "secondary", ONCE);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_CONVERSATION);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_CONVERSATION, ONCE);
	}
}