package net.thomas.portfolio.service_commons.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UidValidatorUnitTest {
	private static final String SOME_PARAMETER_NAME = "parameter";
	private static final boolean NOT_REQUIRED = false;
	private static final boolean REQUIRED = true;
	private static final String SOME_ODD_LENGTH_UID = "A";
	private static final String SOME_VALID_UID = "AA";

	private UidValidator required;
	private UidValidator optional;

	@Before
	public void setUp() {
		optional = new UidValidator(SOME_PARAMETER_NAME, NOT_REQUIRED);
		required = new UidValidator(SOME_PARAMETER_NAME, REQUIRED);
	}

	@Test
	public void shouldAcceptMissingParameter() {
		assertTrue(optional.isValid(null));
		assertTrue(optional.isValid(""));
	}

	@Test
	public void shouldRejectMissingParameter() {
		assertFalse(required.isValid(null));
		assertFalse(required.isValid(""));
	}

	@Test
	public void shouldRejectUidWithOddLength() {
		assertFalse(optional.isValid(SOME_ODD_LENGTH_UID));
	}

	@Test
	public void shouldShowNullUidToNotBeRequired() {
		final String reason = optional.getReason("");
		assertTrue(reason.contains("not required"));
	}

	@Test
	public void shouldShowUidToBeValid() {
		final String reason = optional.getReason(SOME_VALID_UID);
		assertTrue(reason.contains("is valid"));
	}

	@Test
	public void shouldShowUidToBeOfOddLengthAsCauseOfRejection() {
		final String reason = optional.getReason(SOME_ODD_LENGTH_UID);
		assertTrue(reason.contains("is of odd length"));
	}
}
