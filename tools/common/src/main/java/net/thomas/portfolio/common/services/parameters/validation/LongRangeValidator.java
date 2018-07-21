package net.thomas.portfolio.common.services.parameters.validation;

public class LongRangeValidator extends ParameterValidator<Long> {

	private final long min;
	private final long max;
	private final boolean required;

	public LongRangeValidator(String parameterName, long min, long max, boolean required) {
		super(parameterName);
		this.min = min;
		this.max = max;
		this.required = required;
	}

	@Override
	public boolean isValid(Long value) {
		return value == null && !required || value != null && min <= value && value <= max;
	}

	@Override
	public String getReason(Long value) {
		if (value == null) {
			return parameterName + " is missing" + (required ? " and required" : ", but not required");
		} else if (value < min) {
			return parameterName + " ( was " + value + " ) < " + min;
		} else if (value > max) {
			return parameterName + " ( was " + value + " ) > " + max;
		} else {
			return parameterName + " ( was " + value + " ) is valid";
		}
	}
}