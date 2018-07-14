package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldType.PRIMITIVE;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.DECIMAL;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.GEO_LOCATION;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.INTEGER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.STRING;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.PrimitiveType.TIMESTAMP;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(as = PrimitiveField.class)
public class PrimitiveField implements Field {
	public static enum PrimitiveType {
		STRING, INTEGER, DECIMAL, TIMESTAMP, GEO_LOCATION
	}

	private String name;
	private PrimitiveType type;
	private boolean isArray;
	private boolean isKeyComponent;

	public PrimitiveField() {
	}

	public static PrimitiveField string(String name) {
		return new PrimitiveField(name, STRING, false, true);
	}

	public static PrimitiveField integer(String name) {
		return new PrimitiveField(name, INTEGER, false, true);
	}

	public static PrimitiveField decimal(String name) {
		return new PrimitiveField(name, DECIMAL, false, true);
	}

	public static PrimitiveField timestamp(String name) {
		return new PrimitiveField(name, TIMESTAMP, false, true);
	}

	public static PrimitiveField geoLocation(String name) {
		return new PrimitiveField(name, GEO_LOCATION, false, true);
	}

	public static PrimitiveField createNonKeyField(String name, PrimitiveType type) {
		return new PrimitiveField(name, type, false, false);
	}

	public static PrimitiveField createNonKeyArrayField(String name, PrimitiveType type) {
		return new PrimitiveField(name, type, true, false);
	}

	public static PrimitiveField createArrayField(String name, PrimitiveType type) {
		return new PrimitiveField(name, type, true, true);
	}

	private PrimitiveField(String name, PrimitiveType type, boolean isArray, boolean isKeyComponent) {
		this.name = name;
		this.type = type;
		this.isArray = isArray;
		this.isKeyComponent = isKeyComponent;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(PrimitiveType type) {
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
		return PRIMITIVE;
	}

	@Override
	public String getName() {
		return name;
	}

	public PrimitiveType getType() {
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
		hash = 37 * hash + type.ordinal();
		hash = 37 * hash + (isArray ? 1 : 0);
		hash = 37 * hash + (isKeyComponent ? 1 : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PrimitiveField) {
			final PrimitiveField other = (PrimitiveField) obj;
			return name.equals(other.name) && type == other.type && isArray == other.isArray && isKeyComponent == other.isKeyComponent;
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