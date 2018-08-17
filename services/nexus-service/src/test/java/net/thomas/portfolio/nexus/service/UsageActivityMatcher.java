package net.thomas.portfolio.nexus.service;

import org.mockito.ArgumentMatcher;

import net.thomas.portfolio.shared_objects.usage_data.UsageActivity;
import net.thomas.portfolio.shared_objects.usage_data.UsageActivityType;

public class UsageActivityMatcher implements ArgumentMatcher<UsageActivity> {
	private final String userId;
	private final UsageActivityType activityType;

	public UsageActivityMatcher(String userId, UsageActivityType activityType) {
		this.userId = userId;
		this.activityType = activityType;
	}

	public static ArgumentMatcher<UsageActivity> matchesActivity(String userId, UsageActivityType activityType) {
		return new UsageActivityMatcher(userId, activityType);
	}

	@Override
	public boolean matches(UsageActivity activity) {
		return userId.equals(activity.user) && activityType == activity.type;
	}
}