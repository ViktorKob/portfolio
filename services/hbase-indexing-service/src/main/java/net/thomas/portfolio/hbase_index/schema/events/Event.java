package net.thomas.portfolio.hbase_index.schema.events;

import net.thomas.portfolio.hbase_index.schema.Entity;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public abstract class Event extends Entity {
	@PartOfKey
	public final Timestamp timeOfEvent;
	public final Timestamp timeOfInterception;

	public Event(Timestamp timeOfEvent, Timestamp timeOfInterception) {
		this.timeOfEvent = timeOfEvent;
		this.timeOfInterception = timeOfInterception;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (timeOfEvent == null ? 0 : timeOfEvent.hashCode());
		result = prime * result + (timeOfInterception == null ? 0 : timeOfInterception.hashCode());
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
		if (!(obj instanceof Event)) {
			return false;
		}
		final Event other = (Event) obj;
		if (timeOfEvent == null) {
			if (other.timeOfEvent != null) {
				return false;
			}
		} else if (!timeOfEvent.equals(other.timeOfEvent)) {
			return false;
		}
		if (timeOfInterception == null) {
			if (other.timeOfInterception != null) {
				return false;
			}
		} else if (!timeOfInterception.equals(other.timeOfInterception)) {
			return false;
		}
		return true;
	}
}