package net.thomas.portfolio.shared_objects.usage_data;

import static java.util.Collections.emptyList;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsageActivities {
	private List<UsageActivity> activities;

	public UsageActivities() {
		activities = emptyList();
	}

	public UsageActivities(List<UsageActivity> activities) {
		this.activities = activities;
	}

	public List<UsageActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<UsageActivity> activities) {
		this.activities = activities;
	}

	public int size() {
		return activities.size();
	}

	@JsonIgnore
	public UsageActivity get(int index) {
		return activities.get(0);
	}

	@Override
	public int hashCode() {
		return activities.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return activities.equals(obj);
	}

	@Override
	public String toString() {
		return activities.toString();
	}
}