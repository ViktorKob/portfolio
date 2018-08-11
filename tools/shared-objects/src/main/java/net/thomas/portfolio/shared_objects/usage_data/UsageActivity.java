package net.thomas.portfolio.shared_objects.usage_data;

import static net.thomas.portfolio.common.utils.ToStringUtil.asString;

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
		final int prime = 31;
		int result = 1;
		result = prime * result + (timeOfActivity == null ? 0 : timeOfActivity.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
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
		if (!(obj instanceof UsageActivity)) {
			return false;
		}
		final UsageActivity other = (UsageActivity) obj;
		if (timeOfActivity == null) {
			if (other.timeOfActivity != null) {
				return false;
			}
		} else if (!timeOfActivity.equals(other.timeOfActivity)) {
			return false;
		}
		if (type != other.type) {
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