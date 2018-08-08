package net.thomas.portfolio.common.services.parameters.validation;

import static net.thomas.portfolio.common.services.parameters.validation.EnumStringValueValidatorUnitTest.TestEnum.A_VALUE;
import static net.thomas.portfolio.common.services.parameters.validation.EnumStringValueValidatorUnitTest.TestEnum.B_VALUE;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EnumStringValueValidatorUnitTest {
	private static final String SOME_PARAMETER = "someParameter";
	private EnumStringValueValidator<TestEnum> required;
	private EnumStringValueValidator<TestEnum> optional;

	@Before
	public void setUpForTest() {
		required = new EnumStringValueValidator<>(SOME_PARAMETER, TestEnum.class, TestEnum.values(), true);
		optional = new EnumStringValueValidator<>(SOME_PARAMETER, TestEnum.class, TestEnum.values(), false);
	}

	@Test
	public void shouldAcceptValidParameter() {
		assertTrue(required.isValid(A_VALUE.name()));
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
	public void shouldRejectRequiredParameterSetToInvalidValue() {
		assertFalse(required.isValid("C"));
	}

	@Test
	public void shouldRejectOptionalParameterSetToInvalidValue() {
		assertFalse(optional.isValid("C"));
	}

	@Test
	public void shouldShowParameterNameWhenValid() {
		String reason = required.getReason(A_VALUE.name());
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenNull() {
		String reason = required.getReason(null);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenInvalid() {
		String reason = required.getReason("C");
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterToBeValid() {
		String reason = required.getReason(A_VALUE.name());
		assertTrue(reason.contains("is valid"));
	}

	@Test
	public void shouldShowValidOptionsForParameter() {
		String reason = required.getReason("C");
		assertTrue(reason.contains(A_VALUE.name()));
		assertTrue(reason.contains(B_VALUE.name()));
	}

	@Test
	public void shouldPrintThatParameterIsRequired() {
		String reason = required.getReason(null);
		assertTrue(reason.contains("is required"));
	}

	@Test
	public void shouldShowParameterToBeOptional() {
		String reason = optional.getReason(null);
		assertTrue(reason.contains("not required"));
	}

	enum TestEnum {
		A_VALUE, B_VALUE
	}
}