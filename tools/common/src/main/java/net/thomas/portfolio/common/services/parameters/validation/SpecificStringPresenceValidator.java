package net.thomas.portfolio.common.services.parameters.validation;

import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.stream.Collectors;

public class SpecificStringPresenceValidator extends StringPresenceValidator {

	private Collection<String> validStrings;
	private String validStringsAsString;

	public SpecificStringPresenceValidator(String parameterName, boolean required) {
		super(parameterName, required);
		validStrings = emptySet();
		validStringsAsString = "";
	}

	public void setValidStrings(Collection<String> validStrings) {
		this.validStrings = validStrings;
		validStringsAsString = "[ " + validStrings.stream()
			.collect(Collectors.joining(", ")) + " ]";
	}

	@Override
	public boolean isValid(String dataType) {
		return super.isValid(dataType) && (dataType == null || validStrings.contains(dataType));
	}

	@Override
	public String getReason(String value) {
		if (value == null) {
			return parameterName + " is missing" + (required ? " and required" : ", but not required") + ". It should belong to " + validStringsAsString;
		} else if (value.isEmpty()) {
			return parameterName + " is empty" + (required ? " and required" : ", but not required") + ". It should belong to " + validStringsAsString;
		} else if (!validStrings.contains(value)) {
			return parameterName + " ( was " + value + " ) should belong to " + validStringsAsString;
		} else {
			return parameterName + " ( was " + value + " ) is valid";
		}
	}
}