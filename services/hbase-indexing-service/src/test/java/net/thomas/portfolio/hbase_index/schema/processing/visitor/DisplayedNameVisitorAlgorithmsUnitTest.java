package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.TestSampleData;
import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

public class DisplayedNameVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(TestSampleData.SOME_DISPLAYED_NAME);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnLocalnameOnce() {
		visit(TestSampleData.SOME_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, "name", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, "name", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, "name", ZERO_TIMES);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(TestSampleData.SOME_DISPLAYED_NAME);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(TestSampleData.SOME_DISPLAYED_NAME);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, TestSampleData.SOME_DISPLAYED_NAME, ONCE);
	}
}