package net.thomas.portfolio.hbase_index.fake;

import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields.fields;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.geoLocation;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.integer;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.dataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.nonKeyDataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.fields.ReferenceField.nonKeyDataTypeArray;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaBuilder;

public class FakeHbaseIndexSchemaFactory {

	public static HbaseIndexSchema buildSchema() {
		HbaseIndexSchemaBuilder builder = new HbaseIndexSchemaBuilder();
		builder = addFields(builder);
		builder = addTypePredicates(builder);
		builder = addEmailIndexables(builder, "Localname", "DisplayedName", "Domain", "EmailAddress");
		builder = addTextMessageIndexables(builder, "PublicId", "PrivateId");
		builder = addConversationIndexables(builder, "PublicId", "PrivateId");
		builder = addSimpleRepresentableParsers(builder);
		return builder.build();
	}

	private static HbaseIndexSchemaBuilder addFields(final HbaseIndexSchemaBuilder builder) {
		builder.addFields("Localname", fields(string("name")));
		builder.addFields("DisplayedName", fields(string("name")));
		builder.addFields("Domain", fields(string("domainPart"), dataType("domain", "Domain")));
		builder.addFields("EmailAddress", fields(dataType("localname", "Localname"), dataType("domain", "Domain")));
		builder.addFields("EmailEndpoint", fields(dataType("displayedName", "DisplayedName"), dataType("address", "EmailAddress")));
		builder.addFields("PublicId", fields(string("number")));
		builder.addFields("PrivateId", fields(string("number")));
		builder.addFields("CommunicationEndpoint", fields(dataType("publicId", "PublicId"), dataType("privateId", "PrivateId")));
		builder.addFields("Email", fields(string("subject"), string("message"), dataType("from", "EmailEndpoint"), nonKeyDataTypeArray("to", "EmailEndpoint"),
				nonKeyDataTypeArray("cc", "EmailEndpoint"), nonKeyDataTypeArray("bcc", "EmailEndpoint")));
		builder.addFields("TextMessage", fields(string("message"), dataType("sender", "CommunicationEndpoint"), dataType("receiver", "CommunicationEndpoint"),
				geoLocation("senderLocation"), geoLocation("receiverLocation")));
		builder.addFields("Conversation", fields(integer("durationIsSeconds"), dataType("primary", "CommunicationEndpoint"),
				nonKeyDataType("secondary", "CommunicationEndpoint"), geoLocation("primaryLocation"), geoLocation("secondaryLocation")));
		return builder;
	}

	private static HbaseIndexSchemaBuilder addTypePredicates(final HbaseIndexSchemaBuilder builder) {
		builder.addDocumentTypes("Email", "TextMessage", "Conversation");
		builder.addSelectorTypes("Localname", "DisplayedName", "Domain", "EmailAddress", "PublicId", "PrivateId");
		builder.addSimpleRepresentableTypes("Localname", "DisplayedName", "Domain", "EmailAddress", "PublicId", "PrivateId");
		return builder;
	}

	private static HbaseIndexSchemaBuilder addEmailIndexables(HbaseIndexSchemaBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.addIndexable(selectorType, "send", "Email", "from");
			builder.addIndexable(selectorType, "received", "Email", "to");
			builder.addIndexable(selectorType, "ccReceived", "Email", "cc");
			builder.addIndexable(selectorType, "bccReceived", "Email", "bcc");
		}
		return builder;
	}

	private static HbaseIndexSchemaBuilder addTextMessageIndexables(HbaseIndexSchemaBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.addIndexable(selectorType, "send", "TextMessage", "sender");
			builder.addIndexable(selectorType, "received", "TextMessage", "receiver");
		}
		return builder;
	}

	private static HbaseIndexSchemaBuilder addConversationIndexables(HbaseIndexSchemaBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.addIndexable(selectorType, "primary", "Conversation", "primary");
			builder.addIndexable(selectorType, "secondary", "Conversation", "secondary");
		}
		return builder;
	}

	private static HbaseIndexSchemaBuilder addSimpleRepresentableParsers(HbaseIndexSchemaBuilder builder) {
		builder.addStringFieldParser("Localname", "name")
			.addStringFieldParser("DisplayedName", "name")
			.addIntegerFieldParser("PublicId", "number")
			.addIntegerFieldParser("PrivateId", "number")
			.addDomainParser()
			.addEmailAddressParser();
		return builder;
	}
}