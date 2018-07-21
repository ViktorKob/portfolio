package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = RawDataType.class, using = DataTypeDeserializer.class)
public class RawDataType extends DataType {

	public RawDataType() {
	}

	public RawDataType(DataTypeId id, Map<String, Object> fields) {
		super(id, fields);
	}
}