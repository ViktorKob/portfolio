package net.thomas.portfolio.common.services.parameters.validation;

import static net.thomas.portfolio.common.services.parameters.validation.EnumValueValidatorUnitTest.TestEnum.A_VALUE;
import static net.thomas.portfolio.common.services.parameters.validation.EnumValueValidatorUnitTest.TestEnum.B_VALUE;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EnumValueValidatorUnitTest {
	private static final String SOME_PARAMETER = "someParameter";
	private EnumValueValidator<TestEnum> required;
	private EnumValueValidator<TestEnum> optional;

	@Before
	public void setUpForTest() {
		required = new EnumValueValidator<>(SOME_PARAMETER,  TestEnum.values(), true);
		optional = new EnumValueValidator<>(SOME_PARAMETER, TestEnum.values(), false);
	}
	
	@Test
	public void shouldAcceptValidParameter() {
		assertTrue(required.isValid(A_VALUE));
	}
	
	@Test
	public void shouldRejectRequiredParameterSetToNull() {
		assertFalse(required.isValid(null));
	}
	
	@Test
	public void shouldAcceptOptionalParameterSetToNull() {
		assertTrue(optional.isValid(null));
	}
	
	@Test
	public void shouldShowParameterNameWhenValid() {
		String reason = required.getReason(A_VALUE);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenNull() {
		String reason = required.getReason(null);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterToBeValid() {
		String reason = required.getReason(A_VALUE);
		assertTrue(reason.contains("is valid"));
	}
	
	@Test
	public void shouldShowValidOptionsForParameter() {
		String reason = required.getReason(null);
		assertTrue(reason.contains(A_VALUE.name()));
		assertTrue(reason.contains(B_VALUE.name()));
	}
	
	@Test
	public void shouldShowParameterToBeRequired() {
		String reason = required.getReason(null);
		assertTrue(reason.contains("is required"));
	}
	
	@Test
	public void shouldShowParameterToBeOptional() {
		String reason = optional.getReason(null);
		assertTrue(reason.contains("not required"));
	}
	
	enum TestEnum{
		A_VALUE, B_VALUE
	}
}