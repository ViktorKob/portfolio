package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import static net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeType.DOCUMENT;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;

@JsonDeserialize(as = Document.class)
public class Document extends DataType {
	private static final long serialVersionUID = 1L;

	public Document() {
	}

	public Document(String type) {
		super(DOCUMENT, type);
	}

	public void setTimeOfEvent(long timeOfEvent) {
		put("timeOfEvent", timeOfEvent);
	}

	public void setTimeOfInterception(long timeOfInterception) {
		put("timeOfInterception", timeOfInterception);
	}

	public long getTimeOfEvent() {
		return (long) get("timeOfEvent");
	}

	public long getTimeOfInterception() {
		return (long) get("timeOfInterception");
	}

	@Override
	public String toString() {
		return getType() + " (" + getUid() + ")@" + getTimeOfEvent() + ": " + super.toString();
	}
}