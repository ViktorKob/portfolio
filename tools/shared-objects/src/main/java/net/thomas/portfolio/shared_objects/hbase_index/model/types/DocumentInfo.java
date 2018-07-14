package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInfo {

	private DataTypeId id;
	private long timeOfEvent;
	private long timeOfInterception;

	public DocumentInfo() {
	}

	public DocumentInfo(DataTypeId id, long timeOfEvent, long timeOfInterception) {
		this.id = id;
		this.timeOfEvent = timeOfEvent;
		this.timeOfInterception = timeOfInterception;
	}

	public DataTypeId getId() {
		return id;
	}

	public void setId(DataTypeId id) {
		this.id = id;
	}

	public long getTimeOfEvent() {
		return timeOfEvent;
	}

	public void setTimeOfEvent(long timeOfEvent) {
		this.timeOfEvent = timeOfEvent;
	}

	public long getTimeOfInterception() {
		return timeOfInterception;
	}

	public void setTimeOfInterception(long timeOfInterception) {
		this.timeOfInterception = timeOfInterception;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DocumentInfo) {
			return id.equals(((DocumentInfo) obj).id);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return id + "@" + getTimeOfEvent() + ": " + super.toString();
	}
}