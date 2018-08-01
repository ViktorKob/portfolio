package net.thomas.portfolio.hbase_index.schema.processing.visitor;

import static net.thomas.portfolio.hbase_index.schema.TestSampleData.SOME_DOMAIN;
import static net.thomas.portfolio.hbase_index.schema.TestSampleData.SOME_TOP_LEVEL_DOMAIN;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.processing.visitor.utils.InvocationCountingContext;

public class DomainVisitorAlgorithmsUnitTest extends VisitorAlgorithmUnitTest {

	@Before
	public final void buildAlgorithm() {
		countingContexts = new InvocationCountingContext[algorithms.size()];
	}

	@Test
	public void shouldInvokePreEntityActionOnce() {
		visit(SOME_DOMAIN);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_PRE_ACTION_ON, SOME_DOMAIN, ONCE);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnMessageOnce() {
		visit(SOME_DOMAIN);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "domainPart", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "domainPart", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "domainPart", ZERO_TIMES);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_DOMAIN);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "uid", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "uid", ZERO_TIMES);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnFromOnce() {
		visit(SOME_DOMAIN);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "domain", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "domain", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "domain", ONCE);
	}

	@Test
	public void shouldSurviveMissingDomain() {
		visit(SOME_TOP_LEVEL_DOMAIN);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "domain", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "domain", ZERO_TIMES);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "domain", ZERO_TIMES);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_DOMAIN);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_DOMAIN, ONCE);
	}
}