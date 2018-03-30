package net.thomas.common.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSerializableParameter implements Parameter {
	private final String name;
	private final Object[] values;

	public JsonSerializableParameter(String name, Object... values) {
		this.name = name;
		this.values = convertValuesToJson(values);
	}

	private Object[] convertValuesToJson(Object... values) {
		final Object[] valuesAsJson = new Object[values.length];
		final ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i < values.length; i++) {
			try {
				valuesAsJson[i] = mapper.writeValueAsString(values[i]);
			} catch (final JsonProcessingException e) {
				throw new RuntimeException("Unable to add parameter: " + values[i], e);
			}
		}
		return valuesAsJson;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object[] getValues() {
		return values;
	}
}