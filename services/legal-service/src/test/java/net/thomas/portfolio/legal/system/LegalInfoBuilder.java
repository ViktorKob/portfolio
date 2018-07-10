package net.thomas.portfolio.legal.system;

import net.thomas.portfolio.shared_objects.legal.LegalInformation;

public class LegalInfoBuilder {
	static final String JUSTIFICATION = "JUSTIFICATION";
	static final String USER = "USER";

	public String user;
	public String justification;
	public Long lowerBound;
	public Long upperBound;

	public LegalInfoBuilder() {
		user = USER;
		justification = null;
		lowerBound = null;
		upperBound = null;
	}

	public LegalInfoBuilder setNullUser() {
		user = null;
		return this;
	}

	public LegalInfoBuilder setEmptyUser() {
		user = "";
		return this;
	}

	public LegalInfoBuilder setValidUser() {
		user = USER;
		return this;
	}

	public LegalInfoBuilder setNullJustification() {
		justification = null;
		return this;
	}

	public LegalInfoBuilder setEmptyJustification() {
		justification = "";
		return this;
	}

	public LegalInfoBuilder setValidJustification() {
		justification = JUSTIFICATION;
		return this;
	}

	public LegalInformation build() {
		return new LegalInformation(user, justification, lowerBound, upperBound);
	}
}