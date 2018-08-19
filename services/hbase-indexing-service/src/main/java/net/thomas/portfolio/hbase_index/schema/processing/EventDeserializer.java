package net.thomas.portfolio.hbase_index.schema.processing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.ThreadSafe;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.hbase_index.schema.events.Conversation;
import net.thomas.portfolio.hbase_index.schema.events.Email;
import net.thomas.portfolio.hbase_index.schema.events.Event;
import net.thomas.portfolio.hbase_index.schema.events.TextMessage;
import net.thomas.portfolio.hbase_index.schema.meta.CommunicationEndpoint;
import net.thomas.portfolio.hbase_index.schema.meta.EmailEndpoint;
import net.thomas.portfolio.hbase_index.schema.selectors.DisplayedName;
import net.thomas.portfolio.hbase_index.schema.selectors.Domain;
import net.thomas.portfolio.hbase_index.schema.selectors.EmailAddress;
import net.thomas.portfolio.hbase_index.schema.selectors.Localname;
import net.thomas.portfolio.hbase_index.schema.selectors.PrivateId;
import net.thomas.portfolio.hbase_index.schema.selectors.PublicId;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.GeoLocation;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Timestamp;

@ThreadSafe
public class EventDeserializer extends JsonDeserializer<Event> {

	private static final Map<String, Deserializer<? extends Event>> DESERIALIZERS;

	static {
		DESERIALIZERS = new HashMap<>();
		final Deserializer<Timestamp> timestamp = (node) -> {
			final JsonNode time = node.get("t");
			final JsonNode timezone = node.get("z");
			return new Timestamp(time.asLong(), timezone.asText());
		};
		final Deserializer<GeoLocation> geoLocation = (node) -> {
			final JsonNode longitude = node.get("x");
			final JsonNode latitude = node.get("y");
			return new GeoLocation(longitude.asDouble(), latitude.asDouble());
		};
		final Deserializer<Localname> localname = (node) -> {
			final JsonNode name = node.get("n");
			final Localname outputNode = new Localname(name.asText());
			outputNode.uid = node.get("id").asText();
			return outputNode;
		};
		final Deserializer<DisplayedName> displayedName = (node) -> {
			final JsonNode name = node.get("n");
			final DisplayedName outputNode = new DisplayedName(name.asText());
			outputNode.uid = node.get("id").asText();
			return outputNode;
		};
		final Deserializer<PublicId> publicId = (node) -> {
			final JsonNode number = node.get("n");
			final PublicId outputNode = new PublicId(number.asText());
			outputNode.uid = node.get("id").asText();
			return outputNode;
		};
		final Deserializer<PrivateId> privateId = (node) -> {
			final JsonNode number = node.get("n");
			final PrivateId outputNode = new PrivateId(number.asText());
			outputNode.uid = node.get("id").asText();
			return outputNode;
		};
		final Deserializer<Domain> domain = new DomainDeserializer();
		final Deserializer<EmailAddress> emailAddress = (node) -> {
			final EmailAddress outputNode = new EmailAddress(ifPresent(localname, node, "l"), ifPresent(domain, node, "d"));
			outputNode.uid = node.get("id").asText();
			return outputNode;
		};
		final Deserializer<EmailEndpoint> emailEndpoint = (node) -> {
			final EmailEndpoint outputNode = new EmailEndpoint(ifPresent(displayedName, node, "d"), ifPresent(emailAddress, node, "a"));
			outputNode.uid = node.get("id").asText();
			return outputNode;
		};
		final Deserializer<CommunicationEndpoint> communicationEndpoint = (node) -> {
			final CommunicationEndpoint outputNode = new CommunicationEndpoint(ifPresent(publicId, node, "a"), ifPresent(privateId, node, "b"));
			outputNode.uid = node.get("id").asText();
			return outputNode;
		};
		DESERIALIZERS.put("Email", (node) -> {
			final JsonNode subject = node.get("s");
			final JsonNode message = node.get("m");
			final Email outputNode = new Email(subject.asText(), message.asText(), ifPresent(emailEndpoint, node, "a"), arrayOf(emailEndpoint, node.get("b")),
					arrayOf(emailEndpoint, node.get("c")), arrayOf(emailEndpoint, node.get("d")), timestamp.deserialize(node.get("tOE")),
					timestamp.deserialize(node.get("tOI")));
			outputNode.uid = node.get("id").asText();
			return outputNode;
		});
		DESERIALIZERS.put("TextMessage", (node) -> {
			final JsonNode message = node.get("m");
			final TextMessage outputNode = new TextMessage(message.asText(), ifPresent(communicationEndpoint, node, "a"),
					ifPresent(communicationEndpoint, node, "b"), ifPresent(geoLocation, node, "aL"), ifPresent(geoLocation, node, "bL"),
					timestamp.deserialize(node.get("tOE")), timestamp.deserialize(node.get("tOI")));
			outputNode.uid = node.get("id").asText();
			return outputNode;
		});
		DESERIALIZERS.put("Conversation", (node) -> {
			final JsonNode durationInSeconds = node.get("d");
			final Conversation outputNode = new Conversation(durationInSeconds.asInt(), ifPresent(communicationEndpoint, node, "a"),
					ifPresent(communicationEndpoint, node, "b"), ifPresent(geoLocation, node, "aL"), ifPresent(geoLocation, node, "bL"),
					timestamp.deserialize(node.get("tOE")), timestamp.deserialize(node.get("tOI")));
			outputNode.uid = node.get("id").asText();
			return outputNode;
		});
	}

	@Override
	public Event deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
		final ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		final JsonNode node = mapper.readTree(parser);
		final Event event = DESERIALIZERS.get(node.get("t").asText()).deserialize(node);
		return event;
	}

	private static EmailEndpoint[] arrayOf(final Deserializer<EmailEndpoint> emailEndpoint, final JsonNode node) {
		final EmailEndpoint[] endpoints = new EmailEndpoint[node.size()];
		for (int i = 0; i < endpoints.length; i++) {
			endpoints[i] = emailEndpoint.deserialize(node.get(i));
		}
		return endpoints;
	}

	private static <T> T ifPresent(final Deserializer<T> deserializer, final JsonNode node, final String field) {
		if (node.has(field)) {
			return deserializer.deserialize(node.get(field));
		} else {
			return null;
		}
	}

	@FunctionalInterface
	interface Deserializer<OUTPUT_TYPE> {
		OUTPUT_TYPE deserialize(JsonNode node);
	}

	static class DomainDeserializer implements Deserializer<Domain> {

		@Override
		public Domain deserialize(final JsonNode node) {
			final JsonNode domainPart = node.get("dP");
			final Domain outputNode = new Domain(domainPart.asText(), node.has("d") ? deserialize(node.get("d")) : null);
			outputNode.uid = node.get("id").asText();
			return outputNode;
		}
	}
}