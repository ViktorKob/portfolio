package net.thomas.portfolio.shared_objects.hbase_index.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;

public class DataTypeSerializer extends StdSerializer<DataType> {
	private static final long serialVersionUID = 1L;
	private FieldsSerializer fieldsSerializer;

	public DataTypeSerializer() {
		this(null);
		fieldsSerializer = new FieldsSerializer();
	}

	public DataTypeSerializer(Class<DataType> type) {
		super(type);
	}

	@Override
	public void serialize(DataType entity, JsonGenerator generator, SerializerProvider serializers) throws IOException {
		generator.writeStartObject();
		generator.writeFieldName("dataType");
		generator.writeObject(entity.getClass()
			.getSimpleName());
		generator.writeFieldName("id");
		generator.writeObject(entity.getId());
		if (entity instanceof Document) {
			generator.writeFieldName("timeOfEvent");
			generator.writeNumber(((Document) entity).getTimeOfEvent());
			generator.writeFieldName("timeOfInterception");
			generator.writeNumber(((Document) entity).getTimeOfInterception());
		}
		generator.writeFieldName("fields");
		fieldsSerializer.serialize(entity.getFields(), generator, serializers);
		generator.writeEndObject();
	}
}