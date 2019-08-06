package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Partial information about a specific document including timestamps and the id")
public class DocumentInfo {

	@ApiModelProperty("The id for the document")
	private DataTypeId id;
	@ApiModelProperty("The best guess for when the event referenced by this document occurred")
	private Timestamp timeOfEvent;
	@ApiModelProperty("The exact time at which the event referenced by this document was intercepted")
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