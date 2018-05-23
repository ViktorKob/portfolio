package net.thomas.portfolio.common.services.validation;

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
		if (uid != null && uid.length() % 2 == 1) {
			return parameterName + " ( was " + uid + " ) is of odd length, but HEX numbers must be of even length";
		} else {
			return super.getReason(uid);
		}
	}
}