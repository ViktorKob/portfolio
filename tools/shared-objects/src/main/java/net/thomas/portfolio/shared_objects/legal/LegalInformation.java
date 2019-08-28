package net.thomas.portfolio.shared_objects.legal;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
@EqualsAndHashCode
@ApiModel(description = "Container for relevant legal information justifying the execution of an action")
public class LegalInformation implements ParameterGroup {
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0", hidden = true)
	public String user;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0", hidden = true)
	public String justification;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0", hidden = true)
	public Long lowerBound;
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "0", hidden = true)
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

	@ApiModelProperty(value = "The lower bound for the scope of the action as a Unix timestamp with milliseconds", example = "1415463675")
	public Long getLi_lowerBound() {
		return lowerBound;
	}

	public void setLi_lowerBound(Long lowerBound) {
		this.lowerBound = lowerBound;
	}

	@ApiModelProperty(value = "The upper bound for the scope of the action as a Unix timestamp with milliseconds", example = "1415463675")
	public Long getLi_upperBound() {
		return upperBound;
	}

	public void setLi_upperBound(Long upperBound) {
		this.upperBound = upperBound;
	}

	@Override
	@JsonIgnore
	@ApiModelProperty(value = "Should be ignored", example = "[]", hidden = true)
	public Parameter[] getParameters() {
		return new Parameter[] { new PreSerializedParameter("li_user", user), new PreSerializedParameter("li_justification", justification),
				new PreSerializedParameter("li_lowerBound", lowerBound), new PreSerializedParameter("li_upperBound", upperBound) };
	}

	@Override
	public String toString() {
		return asString(this);
	}
}