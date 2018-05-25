package net.thomas.portfolio.shared_objects.hbase_index.model.types;

import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.DataTypeDeserializer;

@JsonDeserialize(as = RawDataType.class, using = DataTypeDeserializer.class)
public class RawDataType extends DataType {

	public RawDataType() {
	}

	public RawDataType(DataTypeId id, Map<String, Object> fields) {
		super(id, fields);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}