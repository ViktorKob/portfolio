package net.thomas.portfolio.common.services.parameters.validation;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;

public class EnumValueValidator<ENUM_TYPE extends Enum<ENUM_TYPE>> extends ParameterValidator<ENUM_TYPE> {

	private final String values;
	private final boolean required;

	public EnumValueValidator(String parameterName, ENUM_TYPE[] values, boolean required) {
		super(parameterName);
		this.required = required;
		this.values = "[" + Arrays.stream(values)
			.map(Enum::name)
			.collect(joining(", ")) + " ]";
	}

	@Override
	public boolean isValid(ENUM_TYPE value) {
		return value != null;
	}

	@Override
	public String getReason(ENUM_TYPE value) {
		if (value == null) {
			return parameterName + " is missing" + (required ? " and required" : ", but not required") + ". It should belong to " + values;
		} else {
			return parameterName + " ( was " + value + " ) is valid";
		}
	}
}