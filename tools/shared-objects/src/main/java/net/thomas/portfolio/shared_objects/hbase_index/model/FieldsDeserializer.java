package net.thomas.portfolio.shared_objects.hbase_index.model;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FieldsDeserializer extends StdDeserializer<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	private DataTypeDeserializer dataTypeDeSerializer;

	public FieldsDeserializer() {
		this(null);
		dataTypeDeSerializer = new DataTypeDeserializer();
	}

	public FieldsDeserializer(Class<Map<String, Object>> type) {
		super(type);
	}

	@Override
	public Map<String, Object> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		final ObjectNode root = mapper.readTree(parser);
		final Map<String, Object> fields = new LinkedHashMap<>();
		final Iterator<Entry<String, JsonNode>> nodeFields = root.fields();
		while (nodeFields.hasNext()) {
			final Entry<String, JsonNode> fieldEntry = nodeFields.next();
			if (fieldEntry.getValue()
				.has("dataType")) {
				fields.put(fieldEntry.getKey(), dataTypeDeSerializer.deserializeDataType(fieldEntry.getValue(), mapper));
			} else {
				fields.put(fieldEntry.getKey(), mapper.treeToValue(fieldEntry.getValue(), Object.class));
			}
		}
		return fields;
	}
}
