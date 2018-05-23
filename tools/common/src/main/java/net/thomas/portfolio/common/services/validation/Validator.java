package net.thomas.portfolio.common.services.validation;

public interface Validator<TYPE> {
	public boolean isValid(TYPE value);

	public String getReason(TYPE value);
}
