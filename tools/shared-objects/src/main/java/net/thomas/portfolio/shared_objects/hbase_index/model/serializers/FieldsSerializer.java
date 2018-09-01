package net.thomas.portfolio.shared_objects.hbase_index.model.serializers;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class FieldsSerializer extends StdSerializer<Map<String, Object>> {

	private static final long serialVersionUID = 1L;

	public FieldsSerializer() {
		this(null);
	}

	public FieldsSerializer(Class<Map<String, Object>> type) {
		super(type);
	}

	@Override
	public void serialize(Map<String, Object> value, JsonGenerator generator, SerializerProvider provider) throws IOException {
		generator.writeStartObject();
		for (final Entry<String, Object> field : value.entrySet()) {
			final Object fieldValue = field.getValue();
			if (fieldValue != null) {
				writeField(generator, provider, field.getKey(), fieldValue);
			}
		}
		generator.writeEndObject();
	}

	private void writeField(JsonGenerator generator, SerializerProvider provider, String fieldName, final Object fieldValue)
			throws IOException, JsonMappingException {
		generator.writeFieldName(fieldName);
		writeValue(generator, provider, fieldValue);
	}

	private void writeValue(JsonGenerator generator, SerializerProvider provider, final Object fieldValue) throws IOException, JsonMappingException {
		if (isPlural(fieldValue)) {
			generator.writeStartArray();
			for (final Object fieldSubValue : (Collection<?>) fieldValue) {
				writeValue(generator, provider, fieldSubValue);
			}
			generator.writeEndArray();
		} else if (fieldValue instanceof DataType) {
			final DataType subType = (DataType) fieldValue;
			provider.findTypedValueSerializer(DataType.class, false, null).serialize(subType, generator, provider);
		} else {
			generator.writeObject(fieldValue);
		}
	}

	private boolean isPlural(final Object object) {
		return object.getClass().isArray() || object instanceof Collection;
	}
}