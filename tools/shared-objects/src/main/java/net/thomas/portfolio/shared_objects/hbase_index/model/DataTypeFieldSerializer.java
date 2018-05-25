package net.thomas.portfolio.shared_objects.hbase_index.model;

import java.io.IOException;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DataTypeFieldSerializer extends StdSerializer<DataType> {
	private static final long serialVersionUID = 1L;

	public DataTypeFieldSerializer() {
		this(null);
	}

	public DataTypeFieldSerializer(Class<DataType> type) {
		super(type);
	}

	@Override
	public void serialize(DataType entity, JsonGenerator generator, SerializerProvider serializers) throws IOException {
		generator.writeStartObject();
		// generator.writeFieldName("id");
		// generator.writeObject(entity.getId());
		for (final Entry<String, Object> entry : entity.getFields()
			.entrySet()) {
			if (entry.getValue() instanceof DataType) {
				generator.writeFieldName(entry.getKey());
				final DataType dataType = (DataType) entry.getValue();
				generator.writeObject(dataType);
			} else {
				generator.writeFieldName(entry.getKey());
				generator.writeObject(entry.getValue());
			}
		}
		generator.writeEndObject();
	}
}