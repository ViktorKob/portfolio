package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Timestamp {
	private Long timestamp;
	private String zoneId;

	public Timestamp() {
	}

	public Timestamp(Long timestamp, ZoneId zone) {
		this.timestamp = timestamp;
		zoneId = zone.getId();
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	@JsonIgnore
	public ZoneId getZone() {
		return ZoneId.of(zoneId);
	}

	@JsonIgnore
	public void setZone(ZoneId zone) {
		zoneId = zone.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (timestamp == null ? 0 : timestamp.hashCode());
		result = prime * result + (zoneId == null ? 0 : zoneId.hashCode());
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
		final Timestamp other = (Timestamp) obj;
		if (timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!timestamp.equals(other.timestamp)) {
			return false;
		}
		if (zoneId == null) {
			if (other.zoneId != null) {
				return false;
			}
		} else if (!zoneId.equals(other.zoneId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Timestamp [timestamp=" + timestamp + ", zone=" + zoneId + "]";
	}
}