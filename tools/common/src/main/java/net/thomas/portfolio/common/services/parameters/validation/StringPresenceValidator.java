package net.thomas.portfolio.common.services.parameters.validation;

public class StringPresenceValidator extends ParameterValidator<String> {

	protected final boolean required;

	public StringPresenceValidator(String parameterName, boolean required) {
		super(parameterName);
		this.required = required;
	}

	@Override
	public boolean isValid(String value) {
		return value != null && !value.isEmpty() || !required;
	}

	@Override
	public String getReason(String value) {
		if (value == null) {
			return parameterName + " is missing" + (required ? " and required" : ", but not required");
		} else if (value.isEmpty()) {
			return parameterName + " is empty" + (required ? " and required" : ", but not required");
		} else {
			return parameterName + " ( was " + value + " ) is valid";
		}
	}
}