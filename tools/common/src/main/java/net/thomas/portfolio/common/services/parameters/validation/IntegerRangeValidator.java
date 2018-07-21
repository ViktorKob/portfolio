package net.thomas.portfolio.common.services.parameters.validation;

public class IntegerRangeValidator extends ParameterValidator<Integer> {

	private final int min;
	private final int max;
	private final boolean required;

	public IntegerRangeValidator(String parameterName, int min, int max, boolean required) {
		super(parameterName);
		this.min = min;
		this.max = max;
		this.required = required;
	}

	@Override
	public boolean isValid(Integer value) {
		return value == null && !required || value != null && min <= value && value <= max;
	}

	@Override
	public String getReason(Integer value) {
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