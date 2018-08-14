package net.thomas.portfolio.common.services.parameters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.thomas.portfolio.testing_tools.ToStringTestUtil;

public class PreSerializedParameterUnitTest {
	@Test
	public void shouldContainNameAfterInitialization() {
		final PreSerializedParameter parameter = new PreSerializedParameter(SOME_NAME, SOME_VALUE);
		assertEquals(SOME_NAME, parameter.getName());
	}

	@Test
	public void shouldContainValueAfterInitialization() {
		final PreSerializedParameter parameter = new PreSerializedParameter(SOME_NAME, SOME_VALUE);
		assertEquals(SOME_VALUE, parameter.getValue());
	}

	@Test
	public void shouldHaveValidToStringMethod() {
		ToStringTestUtil.assertToStringContainsAllFieldsFromObject(new PreSerializedParameter(SOME_NAME, SOME_VALUE));
	}

	private static final String SOME_NAME = "SOME_NAME";
	private static final String SOME_VALUE = "SOME_VALUE";
}
