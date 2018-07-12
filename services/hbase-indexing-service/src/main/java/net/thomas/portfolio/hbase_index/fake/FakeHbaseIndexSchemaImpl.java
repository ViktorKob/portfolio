package net.thomas.portfolio.hbase_index.fake;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.GEO_LOCATION;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.INTEGER;
import static net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField.PrimitiveType.STRING;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.thomas.portfolio.shared_objects.hbase_index.model.data.Field;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.PrimitiveField;
import net.thomas.portfolio.shared_objects.hbase_index.model.data.ReferenceField;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchemaSerialization;

public class FakeHbaseIndexSchemaImpl extends HbaseIndexSchemaSerialization {
	private static final boolean IS_NOT_ARRAY = false;
	private static final boolean IS_ARRAY = true;
	private static final boolean IS_NOT_PART_OF_KEY = false;
	private static final boolean IS_PART_OF_KEY = true;

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
		dataTypeFields.put("Email", fields(string("subject"), string("message"), dataType("from", "EmailEndpoint"), dataTypeNonKeyArray("to", "EmailEndpoint"),
				dataTypeNonKeyArray("cc", "EmailEndpoint"), dataTypeNonKeyArray("bcc", "EmailEndpoint")));
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

	private PrimitiveField string(String name) {
		return new PrimitiveField(name, STRING, IS_NOT_ARRAY, IS_PART_OF_KEY);
	}

	private Field integer(String name) {
		return new PrimitiveField(name, INTEGER, IS_NOT_ARRAY, IS_PART_OF_KEY);
	}

	private Field geoLocation(String name) {
		return new PrimitiveField(name, GEO_LOCATION, IS_NOT_ARRAY, IS_NOT_PART_OF_KEY);
	}

	private ReferenceField dataType(String name, String type) {
		return new ReferenceField(name, type, IS_NOT_ARRAY, IS_PART_OF_KEY);
	}

	private ReferenceField nonKeyDataType(String name, String type) {
		return new ReferenceField(name, type, IS_NOT_ARRAY, IS_NOT_PART_OF_KEY);
	}

	private ReferenceField dataTypeNonKeyArray(String name, String type) {
		return new ReferenceField(name, type, IS_ARRAY, IS_NOT_PART_OF_KEY);
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