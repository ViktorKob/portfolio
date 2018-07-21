package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import net.thomas.portfolio.shared_objects.hbase_index.model.serializers.FieldDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = FieldDeserializer.class)
public interface Field {
	FieldType getFieldType();

	boolean isKeyComponent();

	String getName();

	boolean isArray();
}
