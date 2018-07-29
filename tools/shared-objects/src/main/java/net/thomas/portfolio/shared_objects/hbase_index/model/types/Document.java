package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = Document.class, using = DataTypeDeserializer.class)
public class Document extends DataType {

	private Timestamp timeOfEvent;
	private Timestamp timeOfInterception;

	public Document() {
	}

	public Document(DataTypeId id) {
		super(id);
	}

	public Document(DataTypeId id, Map<String, Object> fields) {
		super(id, fields);
	}

	public void setTimeOfEvent(Timestamp timeOfEvent) {
		this.timeOfEvent = timeOfEvent;
	}

	public void setTimeOfInterception(Timestamp timeOfInterception) {
		this.timeOfInterception = timeOfInterception;
	}

	public Timestamp getTimeOfEvent() {
		return timeOfEvent;
	}

	public Timestamp getTimeOfInterception() {
		return timeOfInterception;
	}
}