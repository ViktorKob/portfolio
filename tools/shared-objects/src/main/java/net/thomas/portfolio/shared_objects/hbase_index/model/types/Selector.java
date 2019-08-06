package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;
import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = Selector.class, using = DataTypeDeserializer.class)
@ApiModel(description = "A specific selector of some type from the model")
public class Selector extends DataType {

	public Selector() {
	}

	public Selector(DataTypeId id) {
		super(id);
	}

	public Selector(DataTypeId id, Map<String, Object> fields) {
		super(id, fields);
	}
}