package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInfo {

	private DataTypeId id;
	private Timestamp timeOfEvent;
	private Timestamp timeOfInterception;

	public DocumentInfo() {
	}

	public DocumentInfo(DataTypeId id, Timestamp timeOfEvent, Timestamp timeOfInterception) {
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

	public Timestamp getTimeOfEvent() {
		return timeOfEvent;
	}

	public void setTimeOfEvent(Timestamp timeOfEvent) {
		this.timeOfEvent = timeOfEvent;
	}

	public Timestamp getTimeOfInterception() {
		return timeOfInterception;
	}

	public void setTimeOfInterception(Timestamp timeOfInterception) {
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