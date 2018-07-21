package net.thomas.portfolio.service_commons.validation;

import net.thomas.portfolio.common.services.parameters.validation.StringPresenceValidator;

public class UidValidator extends StringPresenceValidator {

	public UidValidator(String parameterName, boolean required) {
		super(parameterName, required);
	}

	@Override
	public boolean isValid(String uid) {
		return super.isValid(uid) && uid.length() % 2 == 0;
	}

	@Override
	public String getReason(String uid) {
		if (uid != null && lengthIsOdd(uid)) {
			return parameterName + " ( was " + uid + " ) is of odd length, but HEX numbers must be of even length";
		} else {
			return super.getReason(uid);
		}
	}

	private boolean lengthIsOdd(String uid) {
		return uid.length() % 2 != 0;
	}
}