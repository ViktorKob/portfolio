package net.thomas.portfolio.shared_objects.hbase_index.model.data;

import static net.thomas.portfolio.shared_objects.hbase_index.model.data.FieldType.PRIMITIVE;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.DECIMAL;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.GEO_LOCATION;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.INTEGER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.STRING;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.TIMESTAMP;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

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
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}