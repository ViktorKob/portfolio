package net.thomas.portfolio.shared_objects.hbase_index.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class DataTypeDeserializer extends JsonDeserializer<DataType> {

	@Override
	public DataType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		final ObjectNode root = mapper.readTree(parser);
		final DataTypeType fieldType = DataTypeType.valueOf(root.get("dataTypeType")
			.asText());
		switch (fieldType) {
		case DOCUMENT:
			return mapper.readValue(root.toString(), Document.class);
		case SELECTOR:
			return mapper.readValue(root.toString(), Selector.class);
		case RAW:
			return mapper.readValue(root.toString(), DataType.class);
		default:
			throw new RuntimeException("Field of type " + fieldType + " cannot be deserialized");
		}
	}
}
