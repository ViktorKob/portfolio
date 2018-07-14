package net.thomas.portfolio.shared_objects.analytics;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalyticalKnowledge {
	@JsonIgnore
	public String alias;
	@JsonIgnore
	public ConfidenceLevel isKnown;
	@JsonIgnore
	public ConfidenceLevel isRestricted;

	public AnalyticalKnowledge() {
	}

	public AnalyticalKnowledge(String alias, ConfidenceLevel isKnown, ConfidenceLevel isRestricted) {
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
	public int hashCode() {
		int hash = alias.hashCode();
		hash = 37 * hash + isKnown.ordinal();
		hash = 37 * hash + isRestricted.ordinal();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AnalyticalKnowledge) {
			final AnalyticalKnowledge other = (AnalyticalKnowledge) obj;
			return alias.equals(other.alias) && isKnown == other.isKnown && isRestricted == other.isRestricted;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return asString(this);
	}
}