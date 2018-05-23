package net.thomas.portfolio.shared_objects.usage_data;

import java.sql.Timestamp;

public class UsageActivityItem {
	private String username;
	private UsageActivityType type;
	private long timeOfActivity;

	public UsageActivityItem() {
	}

	public UsageActivityItem(String username, String type, Timestamp timeOfActivity) {
		this.username = username;
		this.type = UsageActivityType.valueOf(type);
		this.timeOfActivity = timeOfActivity.getTime();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UsageActivityType getType() {
		return type;
	}

	public void setType(UsageActivityType type) {
		this.type = type;
	}

	public long getTimeOfActivity() {
		return timeOfActivity;
	}

	public void setTimeOfActivity(long timeOfActivity) {
		this.timeOfActivity = timeOfActivity;
	}
}