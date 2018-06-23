package net.thomas.portfolio.shared_objects.analytics;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriorKnowledge {
	@JsonIgnore
	public String alias;
	@JsonIgnore
	public ConfidenceLevel isKnown;
	@JsonIgnore
	public ConfidenceLevel isRestricted;

	public PriorKnowledge() {
	}

	public PriorKnowledge(String alias, ConfidenceLevel isKnown, ConfidenceLevel isRestricted) {
		this.alias = alias;
		this.isKnown = isKnown;
		this.isRestricted = isRestricted;
	}

	public String getPk_alias() {
		return alias;
	}

	public void setPk_alias(String alias) {
		this.alias = alias;
	}

	public ConfidenceLevel getPk_isKnown() {
		return isKnown;
	}

	public void setPk_isKnown(ConfidenceLevel isKnown) {
		this.isKnown = isKnown;
	}

	public ConfidenceLevel getPk_isRestricted() {
		return isRestricted;
	}

	public void setPk_isRestricted(ConfidenceLevel isRestricted) {
		this.isRestricted = isRestricted;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PriorKnowledge) {
			final PriorKnowledge other = (PriorKnowledge) obj;
			return alias.equals(other.alias) && isKnown == other.isKnown && isRestricted == other.isRestricted;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}