package net.thomas.portfolio.shared_objects.usage_data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.Parameter;
import net.thomas.portfolio.common.services.ParameterGroup;
import net.thomas.portfolio.common.services.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageActivityItem implements ParameterGroup {
	@JsonIgnore
	public String username;
	@JsonIgnore
	public UsageActivityType type;
	@JsonIgnore
	public Long timeOfActivity;

	public UsageActivityItem() {
	}

	public UsageActivityItem(String username, UsageActivityType type, Long timeOfActivity) {
		this.username = username;
		this.type = type;
		this.timeOfActivity = timeOfActivity;
	}

	public String getUai_username() {
		return username;
	}

	public void setUai_Username(String username) {
		this.username = username;
	}

	public UsageActivityType getUai_type() {
		return type;
	}

	public void setUai_type(UsageActivityType type) {
		this.type = type;
	}

	public Long getUai_timeOfActivity() {
		return timeOfActivity;
	}

	public void setUai_timeOfActivity(Long timeOfActivity) {
		this.timeOfActivity = timeOfActivity;
	}

	@Override
	@JsonIgnore
	public Parameter[] getParameters() {
		return new Parameter[] { new PreSerializedParameter("uai_username", username), new PreSerializedParameter("uai_type", type),
				new PreSerializedParameter("uai_timeOfActivity", timeOfActivity) };
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UsageActivityItem) {
			final UsageActivityItem other = (UsageActivityItem) obj;
			return username.equals(other.username) && type == other.type && timeOfActivity == other.timeOfActivity;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}