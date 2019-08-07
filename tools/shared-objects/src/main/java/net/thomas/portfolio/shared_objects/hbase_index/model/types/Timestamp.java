package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static java.time.ZoneId.of;

import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "A unique point in time measured in signed distance from the epoch in ms in UTC with the original timezone stored as aa string next to it")
public class Timestamp {
	public static final Timestamp UNKNOWN = new Timestamp(0l);

	@ApiModelProperty(value = "Exact time as ms since the epoch in UTC", example = "1415463675")
	private Long timestampInUtc;
	@ApiModelProperty("The original timezone from the source as a Java ZoneId")
	private String originalTimeZoneId;

	public Timestamp() {
	}

	public Timestamp(Long timestampInUtc) {
		this.timestampInUtc = timestampInUtc;
		originalTimeZoneId = of("UTC").getId();
	}

	public Timestamp(Long timestampInUtc, ZoneId zone) {
		this.timestampInUtc = timestampInUtc;
		originalTimeZoneId = zone.getId();
	}

	public Timestamp(long timestampInUtc, String zoneId) {
		this.timestampInUtc = timestampInUtc;
		originalTimeZoneId = zoneId;
	}

	public Long getTimestamp() {
		return timestampInUtc;
	}

	public void setTimestamp(Long timestampInUtc) {
		this.timestampInUtc = timestampInUtc;
	}

	public String getOriginalTimeZoneId() {
		return originalTimeZoneId;
	}

	public void setOriginalTimeZoneId(String originalTimeZoneId) {
		this.originalTimeZoneId = originalTimeZoneId;
	}

	@JsonIgnore
	public ZoneId getOriginalTimeZone() {
		return of(originalTimeZoneId);
	}

	@JsonIgnore
	public void setOriginalTimeZone(ZoneId originalTimeZoneId) {
		this.originalTimeZoneId = originalTimeZoneId.getId();
	}

	public boolean isBefore(Timestamp timestamp) {
		return timestampInUtc < timestamp.getTimestamp();
	}

	public boolean isBefore(Long timestamp) {
		return timestampInUtc < timestamp;
	}

	public boolean isAfter(Timestamp timestamp) {
		return timestampInUtc > timestamp.getTimestamp();
	}

	public boolean isAfter(Long timestamp) {
		return timestampInUtc > timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (timestampInUtc == null ? 0 : timestampInUtc.hashCode());
		result = prime * result + (originalTimeZoneId == null ? 0 : originalTimeZoneId.hashCode());
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
		if (timestampInUtc == null) {
			if (other.timestampInUtc != null) {
				return false;
			}
		} else if (!timestampInUtc.equals(other.timestampInUtc)) {
			return false;
		}
		if (originalTimeZoneId == null) {
			if (other.originalTimeZoneId != null) {
				return false;
			}
		} else if (!originalTimeZoneId.equals(other.originalTimeZoneId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Timestamp [timestamp=" + timestampInUtc + ", zone=" + originalTimeZoneId + "]";
	}
}