package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
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
}