package net.thomas.portfolio.hbase_index.fake;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.geoLocation;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.integer;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.string;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.dataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.nonKeyDataType;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField.nonKeyDataTypeArray;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaSerialization;

public class FakeHbaseIndexSchemaImpl extends HbaseIndexSchemaSerialization {

	public FakeHbaseIndexSchemaImpl() {
		dataTypeFields = new HashMap<>();
		dataTypeFields.put("Localname", fields(string("name")));
		dataTypeFields.put("DisplayedName", fields(string("name")));
		dataTypeFields.put("Domain", fields(string("domainPart"), dataType("domain", "Domain")));
		dataTypeFields.put("EmailAddress", fields(dataType("localname", "Localname"), dataType("domain", "Domain")));
		dataTypeFields.put("EmailEndpoint", fields(dataType("displayedName", "DisplayedName"), dataType("address", "EmailAddress")));
		dataTypeFields.put("PublicId", fields(string("number")));
		dataTypeFields.put("PrivateId", fields(string("number")));
		dataTypeFields.put("CommunicationEndpoint", fields(dataType("publicId", "PublicId"), dataType("privateId", "PrivateId")));
		dataTypeFields.put("Email", fields(string("subject"), string("message"), dataType("from", "EmailEndpoint"), nonKeyDataTypeArray("to", "EmailEndpoint"),
				nonKeyDataTypeArray("cc", "EmailEndpoint"), nonKeyDataTypeArray("bcc", "EmailEndpoint")));
		dataTypeFields.put("TextMessage", fields(string("message"), dataType("sender", "CommunicationEndpoint"), dataType("receiver", "CommunicationEndpoint"),
				geoLocation("senderLocation"), geoLocation("receiverLocation")));
		dataTypeFields.put("Conversation", fields(integer("durationIsSeconds"), dataType("primary", "CommunicationEndpoint"),
				nonKeyDataType("secondary", "CommunicationEndpoint"), geoLocation("primaryLocation"), geoLocation("secondaryLocation")));

		dataTypes = dataTypeFields.keySet();

		documentTypes = setOf("Email", "TextMessage", "Conversation");
		selectorTypes = setOf("Localname", "DisplayedName", "Domain", "EmailAddress", "PublicId", "PrivateId");
		simpleRepresentableTypes = setOf("Localname", "DisplayedName", "Domain", "EmailAddress", "PublicId", "PrivateId");

		final IndexableBuilder builder = new IndexableBuilder();
		createEmailIndexables(builder, "Localname", "DisplayedName", "Domain", "EmailAddress");
		createTextMessageIndexables(builder, "PublicId", "PrivateId");
		createConversationIndexables(builder, "PublicId", "PrivateId");
		indexables = builder.build();
		indexableDocumentTypes = buildIndexableMap(indexables, Indexable::getDocumentType);
		indexableRelations = buildIndexableMap(indexables, Indexable::getPath);
	}

	private Map<String, Set<String>> buildIndexableMap(Map<String, Collection<Indexable>> indexables, Function<? super Indexable, ? extends String> mapper) {
		final HashMap<String, Set<String>> relationMap = new HashMap<>();
		for (final String selectorType : selectorTypes) {
			final Collection<Indexable> selectorIndexables = indexables.get(selectorType);
			relationMap.put(selectorType, selectorIndexables.stream()
				.map(mapper)
				.collect(toSet()));
		}
		return relationMap;
	}

	private Set<String> setOf(String... values) {
		return new HashSet<>(asList(values));
	}

	private LinkedHashMap<String, Field> fields(Field... fields) {
		return stream(fields).collect(toMap(Field::getName, identity(), (oldKey, newKey) -> oldKey, LinkedHashMap::new));
	}

	private void createEmailIndexables(IndexableBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.add(selectorType, "send", "Email", "from");
			builder.add(selectorType, "received", "Email", "to");
			builder.add(selectorType, "ccReceived", "Email", "cc");
			builder.add(selectorType, "bccReceived", "Email", "bcc");
		}
	}

	private void createTextMessageIndexables(IndexableBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.add(selectorType, "send", "TextMessage", "sender");
			builder.add(selectorType, "received", "TextMessage", "receiver");
		}
	}

	private void createConversationIndexables(IndexableBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.add(selectorType, "primary", "Conversation", "primary");
			builder.add(selectorType, "secondary", "Conversation", "secondary");
		}
	}

	@Override
	public Map<String, LinkedHashMap<String, Field>> getDataTypeFields() {
		return dataTypeFields;
	}

	private class IndexableBuilder {
		private final Map<String, Collection<Indexable>> indexables;

		public IndexableBuilder() {
			indexables = new HashMap<>();
		}

		public IndexableBuilder add(String selectorType, String path, String documentType, String documentField) {
			final Indexable indexable = new Indexable(selectorType, path, documentType, documentField);
			updateIndexableBySelector(selectorType, indexable);
			updateIndexableByDocument(documentType, indexable);
			return this;
		}

		private void updateIndexableBySelector(String selectorType, final Indexable indexable) {
			if (!indexables.containsKey(selectorType)) {
				indexables.put(selectorType, new LinkedList<>());
			}
			indexables.get(selectorType)
				.add(indexable);
		}

		private void updateIndexableByDocument(String documentType, final Indexable indexable) {
			if (!indexables.containsKey(documentType)) {
				indexables.put(documentType, new LinkedList<>());
			}
			indexables.get(documentType)
				.add(indexable);
		}

		public Map<String, Collection<Indexable>> build() {
			return indexables;
		}
	}
}