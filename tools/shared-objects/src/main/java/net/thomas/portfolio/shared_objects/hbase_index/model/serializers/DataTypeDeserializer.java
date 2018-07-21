package net.thomas.portfolio.shared_objects.hbase_index.model.serializers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataTypeId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Document;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.RawDataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;

public class DataTypeDeserializer extends StdDeserializer<DataType> {
	private static final long serialVersionUID = 1L;

	public DataTypeDeserializer() {
		this(null);
	}

	public DataTypeDeserializer(Class<DataType> type) {
		super(type);
	}

	@Override
	public DataType deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		final JsonNode node = mapper.readTree(parser);
		return (DataType) deserializeDataType(node, mapper);
	}

	private Object deserializeDataType(JsonNode node, ObjectMapper mapper)
			throws JsonParseException, JsonMappingException, IOException, JsonProcessingException {
		if (node.has("dataType")) {
			switch (node.get("dataType")
				.asText()) {
			case "Document":
				return deserializeDocument(node, mapper);
			case "Selector":
				return deserializeSelector(node, mapper);
			case "RawDataType":
				return deserializeRawDataType(node, mapper);
			default:
				throw new RuntimeException("Unable to deserialize " + node);
			}
		} else if (node instanceof ArrayNode) {
			final List<Object> values = new LinkedList<>();
			final ArrayNode arrayNode = (ArrayNode) node;
			for (final JsonNode subNode : arrayNode) {
				values.add(deserializeDataType(subNode, mapper));
			}
			return values;
		} else {
			return mapper.treeToValue(node, Object.class);
		}
	}

	public Document deserializeDocument(JsonNode node, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException {
		final Document document = (Document) populateWithValues(new Document(), node, mapper);
		document.setTimeOfEvent(node.get("timeOfEvent")
			.asLong(0));
		document.setTimeOfInterception(node.get("timeOfInterception")
			.asLong(0));
		return document;
	}

	public Selector deserializeSelector(JsonNode node, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException {
		return (Selector) populateWithValues(new Selector(), node, mapper);
	}

	public RawDataType deserializeRawDataType(JsonNode node, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException {
		return (RawDataType) populateWithValues(new RawDataType(), node, mapper);
	}

	private DataType populateWithValues(final DataType dataType, JsonNode node, ObjectMapper mapper)
			throws JsonParseException, JsonMappingException, IOException {
		dataType.setId(mapper.treeToValue(node.get("id"), DataTypeId.class));
		dataType.setFields(deserializeFields(node.get("fields"), mapper));
		return dataType;
	}

	public Map<String, Object> deserializeFields(JsonNode node, ObjectMapper mapper) throws JsonParseException, JsonMappingException, IOException {
		final Map<String, Object> fields = new LinkedHashMap<>();
		final Iterator<Entry<String, JsonNode>> sourceFields = node.fields();
		while (sourceFields.hasNext()) {
			final Entry<String, JsonNode> fieldEntry = sourceFields.next();
			fields.put(fieldEntry.getKey(), deserializeDataType(fieldEntry.getValue(), mapper));
		}
		return fields;
	}
}
