package net.thomas.portfolio.common.services.parameters.validation;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SpecificStringPresenceValidatorUnitTest {
	private static final String[] VALID_STRINGS = { "VALID STRING_1", "VALID_STRING_2" };
	private static final String INVALID_STRING = "INVALID_STRING";
	private static final String EMPTY_STRING = "";
	private static final String SOME_PARAMETER = "someParameter";
	private SpecificStringPresenceValidator required;
	private SpecificStringPresenceValidator optional;

	@Before
	public void setUpForTest() {
		required = new SpecificStringPresenceValidator(SOME_PARAMETER, true);
		required.setValidStrings(asList(VALID_STRINGS));
		optional = new SpecificStringPresenceValidator(SOME_PARAMETER, false);
		optional.setValidStrings(asList(VALID_STRINGS));
	}

	@Test
	public void shouldAcceptAllRequiredValidParameters() {
		assertTrue(required.isValid(VALID_STRINGS[0]));
		assertTrue(required.isValid(VALID_STRINGS[1]));
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
	public void shouldRejectRequiredParameterSetToInvalidString() {
		assertFalse(required.isValid(INVALID_STRING));
	}

	@Test
	public void shouldShowParameterNameWhenValid() {
		String reason = required.getReason(VALID_STRINGS[0]);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenNull() {
		String reason = required.getReason(null);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenInvalid() {
		String reason = required.getReason(INVALID_STRING);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterToBeValid() {
		String reason = required.getReason(VALID_STRINGS[0]);
		assertTrue(reason.contains("is valid"));
	}

	@Test
	public void shouldShowValidOptionsForParameter() {
		String reason = required.getReason(INVALID_STRING);
		assertTrue(reason.contains(VALID_STRINGS[0]));
		assertTrue(reason.contains(VALID_STRINGS[1]));
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
