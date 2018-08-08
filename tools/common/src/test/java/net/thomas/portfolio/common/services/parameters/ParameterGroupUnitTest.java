package net.thomas.portfolio.common.services.parameters;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ParameterGroupUnitTest {
	private static final String SOME_PARAMETER_NAME = "parameter name";
	private static final String SOME_VALUE_1 = "Value 1";
	private static final String SOME_VALUE_2 = "Value 2";
	private final Parameter SOME_PARAMETER = new PreSerializedParameter(SOME_PARAMETER_NAME, "value");

	@Test
	public void shouldBuildSimpleGroup() {
		final ParameterGroup group = ParameterGroup.asGroup(SOME_PARAMETER);
		assertEquals(SOME_PARAMETER, group.getParameters()[0]);
	}

	@Test
	public void shouldBuildCollectionGroupWithSameParameterName() {
		final ParameterGroup group = ParameterGroup.asGroup(SOME_PARAMETER_NAME, asList(SOME_VALUE_1, SOME_VALUE_2));
		assertEquals(SOME_PARAMETER_NAME, group.getParameters()[0].getName());
		assertEquals(SOME_PARAMETER_NAME, group.getParameters()[1].getName());
	}

	@Test
	public void shouldBuildCollectionGroupWithMultipleValues() {
		final ParameterGroup group = ParameterGroup.asGroup(SOME_PARAMETER_NAME, asList(SOME_VALUE_1, SOME_VALUE_2));
		assertEquals(SOME_VALUE_1, group.getParameters()[0].getValue());
		assertEquals(SOME_VALUE_2, group.getParameters()[1].getValue());
	}
}
