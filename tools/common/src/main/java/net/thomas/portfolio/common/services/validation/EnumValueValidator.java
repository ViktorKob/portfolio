package net.thomas.portfolio.common.services.validation;

import static java.util.stream.Collectors.joining;

import java.util.Arrays;

public class EnumValueValidator<ENUM_TYPE extends Enum<ENUM_TYPE>> extends ParameterValidator<String> {

	private final Class<ENUM_TYPE> enumType;
	private final String values;
	private final boolean required;

	public EnumValueValidator(String parameterName, Class<ENUM_TYPE> enumType, ENUM_TYPE[] values, boolean required) {
		super(parameterName);
		this.enumType = enumType;
		this.required = required;
		this.values = "[" + Arrays.stream(values)
			.map(Enum::name)
			.collect(joining(", ")) + " ]";
	}

	@Override
	public boolean isValid(String value) {
		return valueOfWorks(value);
	}

	@Override
	public String getReason(String value) {
		if (value == null) {
			return parameterName + " is missing" + (required ? " and required" : ", but not required");
		} else if (!valueOfWorks(value)) {
			return parameterName + " ( was " + value + " ) must belong to " + values;
		} else {
			return parameterName + " ( was " + value + " ) is valid";
		}
	}

	private boolean valueOfWorks(String value) {
		try {
			Enum.valueOf(enumType, value);
			return true;
		} catch (final IllegalArgumentException e) {
			return false;
		}
	}
}