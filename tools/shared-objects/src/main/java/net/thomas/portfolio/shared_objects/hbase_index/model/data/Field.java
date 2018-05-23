package net.thomas.portfolio.shared_objects.hbase_index.model.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FieldDeserializer.class)
public interface Field {
	FieldType getFieldType();

	boolean isKeyComponent();

	String getName();

	boolean isArray();
}
