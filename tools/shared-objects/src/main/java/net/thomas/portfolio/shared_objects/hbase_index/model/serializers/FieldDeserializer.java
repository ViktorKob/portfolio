package net.thomas.portfolio.shared_objects.hbase_index.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.FieldType;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField;

public class FieldDeserializer extends JsonDeserializer<Field> {

	@Override
	public Field deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		final ObjectNode root = mapper.readTree(parser);
		final FieldType fieldType = FieldType.valueOf(root.get("fieldType")
			.asText());
		switch (fieldType) {
		case PRIMITIVE:
			return mapper.readValue(root.toString(), PrimitiveField.class);
		case REFERENCE:
			return mapper.readValue(root.toString(), ReferenceField.class);
		default:
			throw new RuntimeException("Field of type " + fieldType + " cannot be deserialized");
		}
	}
}
