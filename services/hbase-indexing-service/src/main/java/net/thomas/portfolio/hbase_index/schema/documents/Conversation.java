package net.thomas.portfolio.hbase_index.schema.documents;

import net.thomas.portfolio.hbase_index.schema.annotations.IndexablePath;
import net.thomas.portfolio.hbase_index.schema.annotations.PartOfKey;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

public class Conversation extends Event {
	@PartOfKey
	public final Integer durationIsSeconds;
	@PartOfKey
	@IndexablePath("primary")
	public final CommunicationEndpoint primary;
	@IndexablePath("secondary")
	public final CommunicationEndpoint secondary;
	public final GeoLocation primaryLocation;
	public final GeoLocation secondaryLocation;

	public Conversation(Timestamp timeOfEvent, Timestamp timeOfInterception, Integer durationIsSeconds, CommunicationEndpoint primary,
			CommunicationEndpoint secondary, GeoLocation primaryLocation, GeoLocation secondaryLocation) {
		super(timeOfEvent, timeOfInterception);
		this.durationIsSeconds = durationIsSeconds;
		this.primary = primary;
		this.secondary = secondary;
		this.primaryLocation = primaryLocation;
		this.secondaryLocation = secondaryLocation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (durationIsSeconds == null ? 0 : durationIsSeconds.hashCode());
		result = prime * result + (primary == null ? 0 : primary.hashCode());
		result = prime * result + (primaryLocation == null ? 0 : primaryLocation.hashCode());
		result = prime * result + (secondary == null ? 0 : secondary.hashCode());
		result = prime * result + (secondaryLocation == null ? 0 : secondaryLocation.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Conversation other = (Conversation) obj;
		if (durationIsSeconds == null) {
			if (other.durationIsSeconds != null) {
				return false;
			}
		} else if (!durationIsSeconds.equals(other.durationIsSeconds)) {
			return false;
		}
		if (primary == null) {
			if (other.primary != null) {
				return false;
			}
		} else if (!primary.equals(other.primary)) {
			return false;
		}
		if (primaryLocation == null) {
			if (other.primaryLocation != null) {
				return false;
			}
		} else if (!primaryLocation.equals(other.primaryLocation)) {
			return false;
		}
		if (secondary == null) {
			if (other.secondary != null) {
				return false;
			}
		} else if (!secondary.equals(other.secondary)) {
			return false;
		}
		if (secondaryLocation == null) {
			if (other.secondaryLocation != null) {
				return false;
			}
		} else if (!secondaryLocation.equals(other.secondaryLocation)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Conversation [durationIsSeconds=" + durationIsSeconds + ", primary=" + primary + ", secondary=" + secondary + ", primaryLocation="
				+ primaryLocation + ", secondaryLocation=" + secondaryLocation + ", timeOfEvent=" + timeOfEvent + ", timeOfInterception=" + timeOfInterception
				+ ", uid=" + uid + "]";
	}
}