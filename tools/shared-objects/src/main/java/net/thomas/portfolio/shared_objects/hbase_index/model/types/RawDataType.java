package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;
import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = RawDataType.class, using = DataTypeDeserializer.class)
@ApiModel(description = "A meta type from the model relating a set of selectors to a document")
public class RawDataType extends DataType {

	public RawDataType() {
	}

	public RawDataType(DataTypeId id) {
		super(id);
	}

	public RawDataType(DataTypeId id, Map<String, Object> fields) {
		super(id, fields);
	}
}