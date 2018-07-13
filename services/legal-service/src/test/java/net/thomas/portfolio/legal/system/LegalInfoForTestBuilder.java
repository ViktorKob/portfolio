package net.thomas.portfolio.legal.system;

import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class LegalInfoForTestBuilder {
	private static final int HALF_A_YEAR = 180 * 24 * 60 * 60 * 1000;
	static final String JUSTIFICATION = "JUSTIFICATION";
	static final String USER = "USER";

	public String user;
	public String justification;
	public Long lowerBound;
	public Long upperBound;

	public LegalInfoForTestBuilder() {
		user = USER;
		justification = null;
		lowerBound = null;
		upperBound = null;
	}

	public LegalInfoForTestBuilder setNullUser() {
		user = null;
		return this;
	}

	public LegalInfoForTestBuilder setEmptyUser() {
		user = "";
		return this;
	}

	public LegalInfoForTestBuilder setValidUser() {
		user = USER;
		return this;
	}

	public LegalInfoForTestBuilder setNullJustification() {
		justification = null;
		return this;
	}

	public LegalInfoForTestBuilder setEmptyJustification() {
		justification = "";
		return this;
	}

	public LegalInfoForTestBuilder setValidJustification() {
		justification = JUSTIFICATION;
		return this;
	}

	public LegalInfoForTestBuilder setValidLowerBound() {
		lowerBound = System.currentTimeMillis() - HALF_A_YEAR;
		return this;
	}

	public LegalInfoForTestBuilder setValidUpperBound() {
		upperBound = System.currentTimeMillis() + HALF_A_YEAR;
		return this;
	}

	public LegalInformation build() {
		return new LegalInformation(user, justification, lowerBound, upperBound);
	}
}