package net.thomas.portfolio.common.services.parameters.validation;

public interface Validator<TYPE> {
	public boolean isValid(TYPE value);

	public String getReason(TYPE value);
}
