package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.COMMUNICATION_ENDPOINT_MISSING_ADDRESS;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.SOME_COMMUNICATION_ENDPOINT;

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
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPublicIdOnce() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "publicId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "publicId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "publicId", ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnPrivateIdOnce() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "privateId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "privateId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, "privateId", ONCE);
	}

	@Test
	public void shouldSurviveMissingPublicId() {
		visit(COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "publicId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "publicId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "publicId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "privateId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "privateId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_DISPLAYED_NAME, "privateId", ONCE);
	}

	@Test
	public void shouldSurviceMissingPrivateId() {
		visit(COMMUNICATION_ENDPOINT_MISSING_ADDRESS);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "publicId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "publicId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "publicId", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "privateId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "privateId", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, COMMUNICATION_ENDPOINT_MISSING_ADDRESS, "privateId", ZERO_TIMES);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_COMMUNICATION_ENDPOINT);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_COMMUNICATION_ENDPOINT, ONCE);
	}
}