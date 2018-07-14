package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.DomainSimpleRepParser.newDomainParser;
import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.EmailAddressSimpleRepParser.newEmailAddressParser;
import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.PositiveIntegerFieldSimpleRepParser.newPositiveIntegerFieldParser;
import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParser.newStringFieldParser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;

public class ParserDeserializer extends JsonDeserializer<SimpleRepresentationParser> {

	@Override
	public SimpleRepresentationParser deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		final ObjectNode root = mapper.readTree(parser);
		final IdCalculator idCalculator = extractIdCalculator(mapper, root);
		final String implementationClass = asText("implementationClass", root);
		switch (implementationClass) {
		case "StringFieldSimpleRepParser":
			return newStringFieldParser(asText("type", root), asText("field", root), asText("pattern", root), idCalculator);
		case "PositiveIntegerFieldSimpleRepParser":
			return newPositiveIntegerFieldParser(asText("type", root), asText("field", root), asText("pattern", root), idCalculator);
		case "DomainSimpleRepParser":
			return newDomainParser(idCalculator);
		case "EmailAddressSimpleRepParser":
			return newEmailAddressParser(idCalculator);
		default:
			throw new RuntimeException("Parser of type " + implementationClass + " cannot be deserialized");
		}
	}

	private IdCalculator extractIdCalculator(final ObjectMapper mapper, final ObjectNode root) throws IOException, JsonParseException, JsonMappingException {
		final JsonNode idCalculatorJson = root.get("idCalculator");
		final String fieldsAsJson = asString("fields", idCalculatorJson);
		return new IdCalculator(mapper.readValue(fieldsAsJson, Fields.class), asBoolean("keyShouldBeUnique", idCalculatorJson));
	}

	private boolean asBoolean(String fieldName, final JsonNode node) {
		return node.get(fieldName)
			.asBoolean();
	}

	private String asText(String fieldName, final JsonNode node) {
		return node.get(fieldName)
			.asText();
	}

	private String asString(String fieldName, final JsonNode node) {
		return node.get(fieldName)
			.toString();
	}
}