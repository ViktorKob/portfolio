package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.EMAIL_ENDPOINT_MISSING_ADDRESS;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.SOME_EMAIL_ENDPOINT;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

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
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ENDPOINT, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ENDPOINT, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnDisplayedNameOnce() {
		visit(SOME_EMAIL_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ENDPOINT, "displayedName", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ENDPOINT, "displayedName", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, "displayedName", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnAddressOnce() {
		visit(SOME_EMAIL_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_EMAIL_ENDPOINT, "address", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_EMAIL_ENDPOINT, "address", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, "address", ONCE);
	}

	@Test
	public void shouldSurviveMissingDisplayedName() {
		visit(EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "displayedName", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "displayedName", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "displayedName", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "address", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "address", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_DISPLAYED_NAME, "address", ONCE);
	}

	@Test
	public void shouldSurviceMissingEmailAddress() {
		visit(EMAIL_ENDPOINT_MISSING_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "displayedName", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "displayedName", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "displayedName", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "address", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "address", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, EMAIL_ENDPOINT_MISSING_ADDRESS, "address", ZERO_TIMES);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_EMAIL_ENDPOINT);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_EMAIL_ENDPOINT, ONCE);
	}
}