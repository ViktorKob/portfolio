package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeDeserializer;

@JsonDeserialize(as = Document.class, using = DataTypeDeserializer.class)
public class Document extends DataType {

	private long timeOfEvent;
	private long timeOfInterception;

	public Document() {
	}

	public Document(DataTypeId id, Map<String, Object> fields) {
		super(id, fields);
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
	public boolean equals(Object obj) {
		if (obj instanceof Document) {
			final Document other = (Document) obj;
			return super.equals(other) && timeOfEvent == other.timeOfEvent && timeOfInterception == other.timeOfInterception;
		} else {
			return super.equals(obj);
		}
	}

	@Override
	public String toString() {
		return id + "@" + getTimeOfEvent() + ": " + super.toString();
	}
}