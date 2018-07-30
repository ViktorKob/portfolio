package net.thomas.portfolio.hbase_index.schema.visitor;

import org.junit.Before;
import org.junit.Test;

import net.thomas.portfolio.hbase_index.schema.visitor.utils.InvocationCountingContext;

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
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "domainPart", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "domainPart", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "domainPart", NEVER);
	}

	@Test
	public void shouldNeverInvokeAnyFieldActionOnUid() {
		visit(SOME_DOMAIN);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "uid", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "uid", NEVER);
	}

	@Test
	public void shouldInvokeCorrectFieldActionOnFromOnce() {
		visit(SOME_DOMAIN);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "domain", ONCE);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "domain", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "domain", ONCE);
	}

	@Test
	public void shouldSurviveMissingDomain() {
		visit(SOME_TOP_LEVEL_DOMAIN);
		assertThatAllAlgorithms(INVOKED_FIELD_PRE_ACTION_ON, SOME_DOMAIN, "domain", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_SIMPLE_ACTION_ON, SOME_DOMAIN, "domain", NEVER);
		assertThatAllAlgorithms(INVOKED_FIELD_POST_ACTION_ON, SOME_DOMAIN, "domain", NEVER);
	}

	@Test
	public void shouldInvokePostEntityActionOnce() {
		visit(SOME_DOMAIN);
		assertEqualsForAllAlgorithms(INVOKED_ENTITY_POST_ACTION_ON, SOME_DOMAIN, ONCE);
	}
}