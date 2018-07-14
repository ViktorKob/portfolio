package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldType.REFERENCE;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;

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
	public int hashCode() {
		int hash = name.hashCode();
		hash = 37 * hash + type.hashCode();
		hash = 37 * hash + (isArray ? 1 : 0);
		hash = 37 * hash + (isKeyComponent ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReferenceField) {
			final ReferenceField other = (ReferenceField) obj;
			return name.equals(other.name) && type.equals(other.type) && isArray == other.isArray && isKeyComponent == other.isKeyComponent;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		final StandardToStringStyle style = new StandardToStringStyle();
		style.setFieldSeparator(", ");
		style.setUseClassName(false);
		style.setUseIdentityHashCode(false);
		return ReflectionToStringBuilder.toString(this, style);
	}
}