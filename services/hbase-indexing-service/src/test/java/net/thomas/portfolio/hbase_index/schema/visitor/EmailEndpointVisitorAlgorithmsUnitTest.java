package net.thomas.portfolio.hbase_index.schema.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.visitor.utils.InvocationCountingContext;

public class EmailEndpointVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_EMAIL_ENDPOINT);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_EMAIL_ENDPOINT, ONCE);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_EMAIL_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ENDPOINT, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ENDPOINT, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, "uid", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnDisplayedNameOnce() {
		visit(SOME_EMAIL_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ENDPOINT, "displayedName", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ENDPOINT, "displayedName", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, "displayedName", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnAddressOnce() {
		visit(SOME_EMAIL_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ENDPOINT, "address", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ENDPOINT, "address", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, "address", ONCE);
	}

	@Test
	public void shouldSurviveMissingDisplayedName() {
		visit(EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "displayedName", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "displayedName", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "displayedName", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "address", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "address", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "address", ONCE);
	}

	@Test
	public void shouldSurviceMissingEmailAddress() {
		visit(EMAIL_ENDPOINT_MISSING_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "displayedName", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "displayedName", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "displayedName", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "address", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "address", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "address", NEVER);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_EMAIL_ENDPOINT);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, ONCE);
	}
}