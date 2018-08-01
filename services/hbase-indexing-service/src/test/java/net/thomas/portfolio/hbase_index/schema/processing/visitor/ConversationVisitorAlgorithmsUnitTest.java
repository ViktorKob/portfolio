package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.SOME_CONVERSATION;

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
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "timeOfEvent", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "timeOfEvent", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "timeOfEvent", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnTimeOfInterceptionOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "timeOfInterception", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "timeOfInterception", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "timeOfInterception", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnDurationInSecondsOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "durationInSeconds", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "durationInSeconds", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "durationInSeconds", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPrimaryLocationOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "primaryLocation", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "primaryLocation", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "primaryLocation", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnSecondaryLocationOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "secondaryLocation", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "secondaryLocation", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "secondaryLocation", ZERO_TIMES);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPrimaryOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "primary", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "primary", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "primary", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnSecondaryOnce() {
		visit(SOME_CONVERSATION);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_CONVERSATION, "secondary", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_CONVERSATION, "secondary", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_CONVERSATION, "secondary", ONCE);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_CONVERSATION);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_CONVERSATION, ONCE);
	}
}