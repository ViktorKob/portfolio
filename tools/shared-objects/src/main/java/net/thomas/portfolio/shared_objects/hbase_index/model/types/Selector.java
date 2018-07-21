package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.DataTypeDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = Selector.class, using = DataTypeDeserializer.class)
public class Selector extends DataType {

	public Selector() {
	}

	public Selector(DataTypeId id, Map<String, Object> fields) {
		super(id, fields);
	}
}