package net.thomas.portfolio.shared_objects.legal;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.Parameter;
import net.thomas.portfolio.common.services.ParameterGroup;
import net.thomas.portfolio.common.services.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LegalInformation implements ParameterGroup {
	@JsonIgnore
	public String justification;
	@JsonIgnore
	public Long lowerBound;
	@JsonIgnore
	public Long upperBound;

	public LegalInformation() {
	}

	public LegalInformation(String justification, Long lowerBound, Long upperBound) {
		this.justification = justification.trim();
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public String getLi_justification() {
		return justification;
	}

	public void setLi_justification(String justification) {
		this.justification = justification.trim();
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
		return new Parameter[] { new PreSerializedParameter("li_justification", justification), new PreSerializedParameter("li_lowerBound", lowerBound),
				new PreSerializedParameter("li_upperBound", upperBound) };
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