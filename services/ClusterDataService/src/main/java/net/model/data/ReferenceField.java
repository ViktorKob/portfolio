package net.model.data;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ReferenceField implements Field {
	private final String name;
	private final String type;
	private final boolean isArray;
	private final boolean isKeyComponent;

	public ReferenceField(String name, String type, boolean isArray, boolean isKeyComponent) {
		this.name = name;
		this.type = type;
		this.isArray = isArray;
		this.isKeyComponent = isKeyComponent;
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
