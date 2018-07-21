package net.thomas.portfolio.common.services.parameters.validation;

public abstract class ParameterValidator<TYPE> implements Validator<TYPE> {

	protected final String parameterName;

	public ParameterValidator(String parameterName) {
		this.parameterName = parameterName;
	}
}