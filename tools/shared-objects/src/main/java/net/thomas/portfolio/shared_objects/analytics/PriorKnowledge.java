package net.thomas.portfolio.shared_objects.analytics;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriorKnowledge {
	@JsonIgnore
	public String alias;
	@JsonIgnore
	public ConfidenceLevel recognition;
	@JsonIgnore
	public ConfidenceLevel isRestricted;

	public PriorKnowledge() {
	}

	public PriorKnowledge(String alias, ConfidenceLevel recognition, ConfidenceLevel isDanish) {
		this.alias = alias;
		this.recognition = recognition;
		this.isRestricted = isDanish;
	}

	public String getPk_alias() {
		return alias;
	}

	public void setPk_alias(String alias) {
		this.alias = alias;
	}

	public ConfidenceLevel getPk_recognition() {
		return recognition;
	}

	public void setPk_recognition(ConfidenceLevel recognition) {
		this.recognition = recognition;
	}

	public ConfidenceLevel getPk_isDanish() {
		return isRestricted;
	}

	public void setPk_isDanish(ConfidenceLevel isDanish) {
		this.isRestricted = isDanish;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PriorKnowledge) {
			final PriorKnowledge other = (PriorKnowledge) obj;
			return alias.equals(other.alias) && recognition == other.recognition && isRestricted == other.isRestricted;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
