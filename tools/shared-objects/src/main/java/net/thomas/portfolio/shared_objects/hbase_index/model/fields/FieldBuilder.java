package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

public abstract class FieldBuilder<FIELD_TYPE extends Field> {
	protected final String name;
	protected boolean isPartOfKey;
	protected boolean isArray;

	public FieldBuilder(String name) {
		this.name = name;
		isPartOfKey = false;
		isArray = false;
	}

	public FieldBuilder<FIELD_TYPE> markIsPartOfKey() {
		isPartOfKey = true;
		return this;
	}

	public FieldBuilder<FIELD_TYPE> markIsArray() {
		isArray = true;
		return this;
	}

	public abstract FIELD_TYPE build();
}