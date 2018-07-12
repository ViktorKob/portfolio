package net.thomas.portfolio.shared_objects.hbase_index.model.data;

import static net.thomas.portfolio.shared_objects.hbase_index.model.data.FieldType.REFERENCE;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = ReferenceField.class)
public class ReferenceField implements Field {
	private String name;
	private String type;
	private boolean isArray;
	private boolean isKeyComponent;

	public ReferenceField() {
	}

	public static ReferenceField dataType(String name, String type) {
		return new ReferenceField(name, type, false, true);
	}

	public static ReferenceField nonKeyDataType(String name, String type) {
		return new ReferenceField(name, type, false, false);
	}

	public static ReferenceField dataTypeArray(String name, String type) {
		return new ReferenceField(name, type, true, true);
	}

	public static ReferenceField nonKeyDataTypeArray(String name, String type) {
		return new ReferenceField(name, type, true, false);
	}

	private ReferenceField(String name, String type, boolean isArray, boolean isKeyComponent) {
		this.name = name;
		this.type = type;
		this.isArray = isArray;
		this.isKeyComponent = isKeyComponent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}

	public void setKeyComponent(boolean isKeyComponent) {
		this.isKeyComponent = isKeyComponent;
	}

	@Override
	public FieldType getFieldType() {
		return REFERENCE;
	}

	@Override
	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public boolean isKeyComponent() {
		return isKeyComponent;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}