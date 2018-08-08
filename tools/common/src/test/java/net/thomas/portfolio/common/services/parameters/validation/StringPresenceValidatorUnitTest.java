package net.thomas.portfolio.common.services.parameters.validation;

import static net.thomas.portfolio.common.services.parameters.validation.EnumStringValueValidatorUnitTest.TestEnum.A_VALUE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class StringPresenceValidatorUnitTest {
	private static final String SOME_STRING = "Some String";
	private static final String EMPTY_STRING = "";
	private static final String SOME_PARAMETER = "someParameter";
	private StringPresenceValidator required;
	private StringPresenceValidator optional;

	@Before
	public void setUpForTest() {
		required = new StringPresenceValidator(SOME_PARAMETER, true);
		optional = new StringPresenceValidator(SOME_PARAMETER, false);
	}

	@Test
	public void shouldAcceptRequiredValidParameter() {
		assertTrue(required.isValid(SOME_STRING));
	}

	@Test
	public void shouldRejectRequiredParameterSetToEmptyString() {
		assertFalse(required.isValid(EMPTY_STRING));
	}

	@Test
	public void shouldRejectRequiredParameterSetToNull() {
		assertFalse(required.isValid(null));
	}

	@Test
	public void shouldAcceptValidOptionalParameter() {
		assertTrue(optional.isValid(SOME_STRING));
	}

	@Test
	public void shouldAcceptOptionalParameterSetToEmptyString() {
		assertTrue(optional.isValid(EMPTY_STRING));
	}

	@Test
	public void shouldAcceptOptionalParameterSetToNull() {
		assertTrue(optional.isValid(null));
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
	public void shouldShowParameterNameWhenEmpty() {
		String reason = required.getReason(EMPTY_STRING);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterToBeValid() {
		String reason = required.getReason(SOME_STRING);
		assertTrue(reason.contains("is valid"));
	}

	@Test
	public void shouldShowParameterToBeRequiredWhenSetToNull() {
		String reason = required.getReason(null);
		assertTrue(reason.contains("is required"));
	}

	@Test
	public void shouldShowParameterToBeRequiredWhenSetToEmptyString() {
		String reason = required.getReason(EMPTY_STRING);
		assertTrue(reason.contains("is required"));
	}

	@Test
	public void shouldShowParameterToBeOptionalWhenSetToNull() {
		String reason = optional.getReason(null);
		assertTrue(reason.contains("not required"));
	}

	@Test
	public void shouldShowParameterToBeOptionalWhenSetToEmptyString() {
		String reason = optional.getReason(EMPTY_STRING);
		assertTrue(reason.contains("not required"));
	}
}
