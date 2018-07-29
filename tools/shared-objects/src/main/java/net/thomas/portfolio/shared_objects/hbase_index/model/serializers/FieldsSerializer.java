package net.thomas.portfolio.shared_objects.hbase_index.model.serializers;

import java.io.IOException;
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
				writeField(generator, provider, field, fieldValue);
			}
		}
		generator.writeEndObject();
	}

	private void writeField(JsonGenerator generator, SerializerProvider provider, final Entry<String, Object> field, final Object fieldValue)
			throws IOException, JsonMappingException {
		generator.writeFieldName(field.getKey());
		if (isArray(fieldValue)) {
			generator.writeStartArray();
			generator.writeString(fieldValue.toString());
			generator.writeEndArray();
		} else if (fieldValue instanceof DataType) {
			final DataType subType = (DataType) fieldValue;
			provider.findTypedValueSerializer(DataType.class, false, null)
				.serialize(subType, generator, provider);
		} else {
			generator.writeObject(fieldValue);
		}
	}

	private boolean isArray(final Object object) {
		return object.getClass()
			.isArray();
	}
}