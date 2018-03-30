package net.model.types;

import net.model.DataType;

public class Document extends DataType {

	private static final long serialVersionUID = 1L;
	private long timeOfEvent;
	private long timeOfInterception;

	public Document(String type) {
		super(type);
	}

	public void setTimeOfEvent(long timeOfEvent) {
		this.timeOfEvent = timeOfEvent;
	}

	public void setTimeOfInterception(long timeOfInterception) {
		this.timeOfInterception = timeOfInterception;
	}

	public long getTimeOfEvent() {
		return timeOfEvent;
	}

	public long getTimeOfInterception() {
		return timeOfInterception;
	}

	@Override
	public String toString() {
		return type + " (" + uid + ")@" + timeOfEvent + ": " + super.toString();
	}
}