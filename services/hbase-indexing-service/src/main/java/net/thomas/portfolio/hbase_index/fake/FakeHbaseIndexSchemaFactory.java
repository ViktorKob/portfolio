package net.thomas.portfolio.hbase_index.fake;

import static net.thomas.portfolio.shared_objects.hbase_index.model.data.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.geoLocation;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.integer;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.dataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.nonKeyDataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.nonKeyDataTypeArray;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaBuilder;

public class FakeHbaseIndexSchemaFactory {

	public static HbaseIndexSchema buildSchema() {
		final HbaseIndexSchemaBuilder builder = new HbaseIndexSchemaBuilder();
		addFields(builder);
		addTypePredicates(builder);
		addEmailIndexables(builder, "Localname", "DisplayedName", "Domain", "EmailAddress");
		addTextMessageIndexables(builder, "PublicId", "PrivateId");
		addConversationIndexables(builder, "PublicId", "PrivateId");
		return builder.build();
	}

	private static void addFields(final HbaseIndexSchemaBuilder builder) {
		builder.addField("Localname", fields(string("name")));
		builder.addField("DisplayedName", fields(string("name")));
		builder.addField("Domain", fields(string("domainPart"), dataType("domain", "Domain")));
		builder.addField("EmailAddress", fields(dataType("localname", "Localname"), dataType("domain", "Domain")));
		builder.addField("EmailEndpoint", fields(dataType("displayedName", "DisplayedName"), dataType("address", "EmailAddress")));
		builder.addField("PublicId", fields(string("number")));
		builder.addField("PrivateId", fields(string("number")));
		builder.addField("CommunicationEndpoint", fields(dataType("publicId", "PublicId"), dataType("privateId", "PrivateId")));
		builder.addField("Email", fields(string("subject"), string("message"), dataType("from", "EmailEndpoint"), nonKeyDataTypeArray("to", "EmailEndpoint"),
				nonKeyDataTypeArray("cc", "EmailEndpoint"), nonKeyDataTypeArray("bcc", "EmailEndpoint")));
		builder.addField("TextMessage", fields(string("message"), dataType("sender", "CommunicationEndpoint"), dataType("receiver", "CommunicationEndpoint"),
				geoLocation("senderLocation"), geoLocation("receiverLocation")));
		builder.addField("Conversation", fields(integer("durationIsSeconds"), dataType("primary", "CommunicationEndpoint"),
				nonKeyDataType("secondary", "CommunicationEndpoint"), geoLocation("primaryLocation"), geoLocation("secondaryLocation")));
	}

	private static void addTypePredicates(final HbaseIndexSchemaBuilder builder) {
		builder.addDocumentTypes("Email", "TextMessage", "Conversation");
		builder.addSelectorTypes("Localname", "DisplayedName", "Domain", "EmailAddress", "PublicId", "PrivateId");
		builder.addSimpleRepresentableTypes("Localname", "DisplayedName", "Domain", "EmailAddress", "PublicId", "PrivateId");
	}

	private static void addEmailIndexables(HbaseIndexSchemaBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.addIndexable(selectorType, "send", "Email", "from");
			builder.addIndexable(selectorType, "received", "Email", "to");
			builder.addIndexable(selectorType, "ccReceived", "Email", "cc");
			builder.addIndexable(selectorType, "bccReceived", "Email", "bcc");
		}
	}

	private static void addTextMessageIndexables(HbaseIndexSchemaBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.addIndexable(selectorType, "send", "TextMessage", "sender");
			builder.addIndexable(selectorType, "received", "TextMessage", "receiver");
		}
	}

	private static void addConversationIndexables(HbaseIndexSchemaBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.addIndexable(selectorType, "primary", "Conversation", "primary");
			builder.addIndexable(selectorType, "secondary", "Conversation", "secondary");
		}
	}
}