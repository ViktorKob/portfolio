package net.thomas.portfolio.shared_objects.legal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalInformation implements ParameterGroup {
	@JsonIgnore
	public String user;
	@JsonIgnore
	public String justification;
	@JsonIgnore
	public Long lowerBound;
	@JsonIgnore
	public Long upperBound;

	public LegalInformation() {
	}

	public LegalInformation(String user, String justification, Long lowerBound, Long upperBound) {
		setLi_user(user);
		setLi_justification(justification);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public LegalInformation(LegalInformation source) {
		user = source.user;
		justification = source.justification;
		lowerBound = source.lowerBound;
		upperBound = source.upperBound;
	}

	public String getLi_user() {
		return user;
	}

	public void setLi_user(String user) {
		if (user != null) {
			this.user = user.trim();
		} else {
			user = null;
		}
	}

	public String getLi_justification() {
		return justification;
	}

	public void setLi_justification(String justification) {
		if (justification != null) {
			this.justification = justification.trim();
		} else {
			justification = null;
		}
	}

	public Long getLi_lowerBound() {
		return lowerBound;
	}

	public void setLi_lowerBound(Long lowerBound) {
		this.lowerBound = lowerBound;
	}

	public Long getLi_upperBound() {
		return upperBound;
	}

	public void setLi_upperBound(Long upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	public int hashCode() {
		return (int) (justification.hashCode() * lowerBound * upperBound * 1009);
	}

	@Override
	@JsonIgnore
	public Parameter[] getParameters() {
		return new Parameter[] { new PreSerializedParameter("li_user", user), new PreSerializedParameter("li_justification", justification),
				new PreSerializedParameter("li_lowerBound", lowerBound), new PreSerializedParameter("li_upperBound", upperBound) };
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LegalInformation) {
			final LegalInformation other = (LegalInformation) obj;
			return justification.equals(other.justification) && lowerBound.equals(other.lowerBound) && upperBound.equals(other.upperBound);
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}