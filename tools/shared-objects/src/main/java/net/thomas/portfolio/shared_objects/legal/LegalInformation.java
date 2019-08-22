package net.thomas.portfolio.shared_objects.legal;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@ApiModel(description = "Container for relevant legal information justifying the execution of an action")
public class LegalInformation implements ParameterGroup {
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0")
	public String user;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0")
	public String justification;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0")
	public Long lowerBound;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0")
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

	@ApiModelProperty("The ID of the user trying to perform the action")
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

	@ApiModelProperty("The legal justification for performing the action in question")
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

	@ApiModelProperty(value = "The lower bound for the scope of the action as a Unix timestamp", example = "1415463675")
	public Long getLi_lowerBound() {
		return lowerBound;
	}

	public void setLi_lowerBound(Long lowerBound) {
		this.lowerBound = lowerBound;
	}

	@ApiModelProperty(value = "The upper bound for the scope of the action as a Unix timestamp", example = "1415463675")
	public Long getLi_upperBound() {
		return upperBound;
	}

	public void setLi_upperBound(Long upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	@JsonIgnore
	public Parameter[] getParameters() {
		return new Parameter[] { new PreSerializedParameter("li_user", user), new PreSerializedParameter("li_justification", justification),
				new PreSerializedParameter("li_lowerBound", lowerBound), new PreSerializedParameter("li_upperBound", upperBound) };
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (justification == null ? 0 : justification.hashCode());
		result = prime * result + (lowerBound == null ? 0 : lowerBound.hashCode());
		result = prime * result + (upperBound == null ? 0 : upperBound.hashCode());
		result = prime * result + (user == null ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LegalInformation)) {
			return false;
		}
		final LegalInformation other = (LegalInformation) obj;
		if (justification == null) {
			if (other.justification != null) {
				return false;
			}
		} else if (!justification.equals(other.justification)) {
			return false;
		}
		if (lowerBound == null) {
			if (other.lowerBound != null) {
				return false;
			}
		} else if (!lowerBound.equals(other.lowerBound)) {
			return false;
		}
		if (upperBound == null) {
			if (other.upperBound != null) {
				return false;
			}
		} else if (!upperBound.equals(other.upperBound)) {
			return false;
		}
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return asString(this);
	}
}