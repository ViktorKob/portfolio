package net.thomas.portfolio.common.services.parameters.validation;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class LongRangeValidatorUnitTest {
	private static final long LOW_VALUE = 1;
	private static final long LOWER_BOUND = 2;
	private static final long VALID_VALUE = 3;
	private static final long UPPER_BOUND = 4;
	private static final long HIGH_VALUE = 5;
	private static final String SOME_PARAMETER = "someParameter";
	private LongRangeValidator required;
	private LongRangeValidator optional;

	@Before
	public void setUpForTest() {
		required = new LongRangeValidator(SOME_PARAMETER, LOWER_BOUND, UPPER_BOUND, true);
		optional = new LongRangeValidator(SOME_PARAMETER, LOWER_BOUND, UPPER_BOUND, false);
	}

	@Test
	public void shouldAcceptRequiredValidParameter() {
		assertTrue(required.isValid(VALID_VALUE));
	}

	@Test
	public void shouldRejectRequiredParameterSetToLowValue() {
		assertFalse(required.isValid(LOW_VALUE));
	}

	@Test
	public void shouldRejectRequiredParameterSetToHighValue() {
		assertFalse(required.isValid(HIGH_VALUE));
	}

	@Test
	public void shouldAcceptRequiredParameterSetToBoundaryValue() {
		assertTrue(required.isValid(LOWER_BOUND));
		assertTrue(required.isValid(UPPER_BOUND));
	}

	@Test
	public void shouldRejectRequiredParameterSetToNull() {
		assertFalse(required.isValid(null));
	}

	@Test
	public void shouldAcceptValidOptionalParameter() {
		assertTrue(optional.isValid(VALID_VALUE));
	}

	@Test
	public void shouldAcceptOptionalParameterSetToNull() {
		assertTrue(optional.isValid(null));
	}

	@Test
	public void shouldRejectOptionalParameterSetToLowValue() {
		assertFalse(optional.isValid(LOW_VALUE));
	}

	@Test
	public void shouldRejectOptionalParameterSetToHighValue() {
		assertFalse(optional.isValid(HIGH_VALUE));
	}

	@Test
	public void shouldShowParameterNameWhenValid() {
		String reason = required.getReason(VALID_VALUE);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenSetToLowValue() {
		String reason = optional.getReason(LOW_VALUE);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenSetToHighValue() {
		String reason = optional.getReason(HIGH_VALUE);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterNameWhenNull() {
		String reason = required.getReason(null);
		assertTrue(reason.contains(SOME_PARAMETER));
	}

	@Test
	public void shouldShowParameterToBeValid() {
		String reason = required.getReason(VALID_VALUE);
		assertTrue(reason.contains("is valid"));
	}

	@Test
	public void shouldShowParameterToBeRequiredWhenSetToNull() {
		String reason = required.getReason(null);
		assertTrue(reason.contains("is required"));
	}

	@Test
	public void shouldShowParameterToBeOptionalWhenSetToNull() {
		String reason = optional.getReason(null);
		assertTrue(reason.contains("not required"));
	}
}