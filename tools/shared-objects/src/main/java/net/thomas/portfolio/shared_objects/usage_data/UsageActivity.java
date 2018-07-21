package net.thomas.portfolio.shared_objects.usage_data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;
import net.thomas.portfolio.common.services.parameters.PreSerializedParameter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageActivity implements ParameterGroup {
	@JsonIgnore
	public String user;
	@JsonIgnore
	public UsageActivityType type;
	@JsonIgnore
	public Long timeOfActivity;

	public UsageActivity() {
	}

	public UsageActivity(String user, UsageActivityType type, Long timeOfActivity) {
		this.user = user;
		this.type = type;
		this.timeOfActivity = timeOfActivity;
	}

	public String getUai_user() {
		return user;
	}

	public void setUai_user(String user) {
		this.user = user;
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
		return new Parameter[] { new PreSerializedParameter("uai_user", user), new PreSerializedParameter("uai_type", type),
				new PreSerializedParameter("uai_timeOfActivity", timeOfActivity) };
	}

	@Override
	public int hashCode() {
		int hash = user.hashCode();
		hash = 37 * hash + type.ordinal();
		hash = 37 * hash + (int) (timeOfActivity ^ timeOfActivity >>> 32);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UsageActivity) {
			final UsageActivity other = (UsageActivity) obj;
			return user.equals(other.user) && type == other.type
					&& (timeOfActivity == other.timeOfActivity || timeOfActivity != null && timeOfActivity.equals(other.timeOfActivity));
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}