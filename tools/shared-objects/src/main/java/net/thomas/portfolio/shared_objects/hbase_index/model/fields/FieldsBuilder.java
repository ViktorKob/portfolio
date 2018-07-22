package net.thomas.portfolio.shared_objects.hbase_index.model.fields;

import java.util.LinkedHashMap;

public class FieldsBuilder {
	private final LinkedHashMap<String, Field> fields;

	public FieldsBuilder() {
		fields = new LinkedHashMap<>();
	}

	public FieldsBuilder add(Field field) {
		fields.put(field.getName(), field);
		return this;
	}

	public Fields build() {
		return Fields.fields(fields);
	}
}
