package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = Document.class, using = DataTypeDeserializer.class)
@ApiModel(description = "A specific document of some type from the model")
public class Document extends DataType {

	@ApiModelProperty("The best guess for when the event referenced by this document occurred")
	private Timestamp timeOfEvent;
	@ApiModelProperty("The exact time at which the event referenced by this document was intercepted")
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