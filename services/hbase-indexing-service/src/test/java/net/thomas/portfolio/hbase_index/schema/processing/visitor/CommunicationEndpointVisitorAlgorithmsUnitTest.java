package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

public class CommunicationEndpointVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, ONCE);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "uid", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPublicIdOnce() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "publicId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "publicId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "publicId", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPrivateIdOnce() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "privateId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "privateId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "privateId", ONCE);
	}

	@Test
	public void shouldSurviveMissingPublicId() {
		visit(COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "publicId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "publicId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "publicId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "privateId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "privateId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "privateId", ONCE);
	}

	@Test
	public void shouldSurviceMissingPrivateId() {
		visit(COMMUNICATION_ENDPOINT_MISSING_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "publicId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "publicId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "publicId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "privateId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "privateId", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "privateId", NEVER);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, ONCE);
	}
}