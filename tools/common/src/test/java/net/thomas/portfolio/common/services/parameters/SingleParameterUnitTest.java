package net.thomas.portfolio.common.services.parameters;

import static net.thomas.portfolio.testing_tools.ToStringTestUtil.assertToStringContainsAllFieldsFromObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SingleParameterUnitTest {
	private static final boolean TEST_DONE = true;

	@Test
	public void shouldContainNameAfterInitialization() {
		final SingleParameter parameter = new SingleParameter(SOME_NAME, SOME_VALUE);
		assertEquals(SOME_NAME, parameter.getName());
	}

	@Test
	public void shouldContainValueAfterInitialization() {
		final SingleParameter parameter = new SingleParameter(SOME_NAME, SOME_VALUE);
		assertEquals(SOME_VALUE, parameter.getValue());
	}

	@Test
	public void shouldHaveValidToStringMethod() {
		assertToStringContainsAllFieldsFromObject(new SingleParameter(SOME_NAME, SOME_VALUE));
		assertTrue(TEST_DONE); // Here to fix code analysis issue
	}

	private static final String SOME_NAME = "SOME_NAME";
	private static final String SOME_VALUE = "SOME_VALUE";
}
