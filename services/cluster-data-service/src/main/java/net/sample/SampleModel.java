package net.sample;

import static net.model.data.PrimitiveField.PrimitiveType.GEO_LOCATION;
import static net.model.data.PrimitiveField.PrimitiveType.INTEGER;
import static net.model.data.PrimitiveField.PrimitiveType.STRING;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.model.data.Field;
import net.model.data.PrimitiveField;
import net.model.data.ReferenceField;
import net.model.meta_data.Indexable;

public class SampleModel {

	private static final boolean IS_NOT_ARRAY = false;
	private static final boolean IS_ARRAY = true;
	private static final boolean IS_NOT_PART_OF_KEY = false;
	private static final boolean IS_PART_OF_KEY = true;

	public static final Map<String, Map<String, Field>> DATA_TYPE_FIELDS;
	public static final Set<String> DATA_TYPES;
	public static final Set<String> DOCUMENT_TYPES;
	public static final Set<String> SELECTOR_TYPES;
	public static final Set<String> SIMPLE_REPRESENTATION_TYPES;
	public static final Map<String, List<Indexable>> INDEXABLES;

	static {
		DATA_TYPE_FIELDS = new HashMap<>();
		DATA_TYPE_FIELDS.put("Localname", fields(string("name")));
		DATA_TYPE_FIELDS.put("DisplayedName", fields(string("name")));
		DATA_TYPE_FIELDS.put("Domain", fields(string("domainPart"), dataType("domain", "Domain")));
		DATA_TYPE_FIELDS.put("EmailAddress", fields(dataType("localname", "Localname"), dataType("domain", "Domain")));
		DATA_TYPE_FIELDS.put("EmailEndpoint", fields(dataType("displayedName", "DisplayedName"), dataType("address", "EmailAddress")));
		DATA_TYPE_FIELDS.put("Pstn", fields(string("number")));
		DATA_TYPE_FIELDS.put("Imei", fields(string("number")));
		DATA_TYPE_FIELDS.put("Imsi", fields(string("number")));
		DATA_TYPE_FIELDS.put("PstnEndpoint", fields(dataType("pstn", "Pstn"), dataType("imei", "Imei"), dataType("imsi", "Imsi")));
		DATA_TYPE_FIELDS.put("Email", fields(string("subject"), string("message"), dataType("from", "EmailEndpoint"),
				dataTypeNonKeyArray("to", "EmailEndpoint"), dataTypeNonKeyArray("cc", "EmailEndpoint"), dataTypeNonKeyArray("bcc", "EmailEndpoint")));
		DATA_TYPE_FIELDS.put("Sms", fields(string("message"), dataType("sender", "PstnEndpoint"),
				dataType("receiver", "PstnEndpoint"), geoLocation("senderLocation"), geoLocation("receiverLocation")));
		DATA_TYPE_FIELDS.put("Voice",
				fields(integer("durationIsSeconds"), dataType("caller", "PstnEndpoint"), nonKeyDataType("called", "PstnEndpoint"),
						geoLocation("callerLocation"), geoLocation("calledLocation")));

		DATA_TYPES = DATA_TYPE_FIELDS.keySet();

		DOCUMENT_TYPES = new HashSet<>(Arrays.asList("Email", "Sms", "Voice"));
		SELECTOR_TYPES = new HashSet<>(Arrays.asList("Localname", "DisplayedName", "Domain", "EmailAddress", "Pstn", "Imei", "Imsi"));
		SIMPLE_REPRESENTATION_TYPES = new HashSet<>(Arrays.asList("Localname", "DisplayedName", "Domain", "EmailAddress", "Pstn", "Imei", "Imsi"));

		final IndexableBuilder builder = new IndexableBuilder();
		createEmailIndexables(builder, "Localname", "DisplayedName", "Domain", "EmailAddress");
		createSmsIndexables(builder, "Pstn", "Imsi", "Imei");
		createVoiceIndexables(builder, "Pstn", "Imsi", "Imei");
		INDEXABLES = builder.build();
	}

	private static Map<String, Field> fields(Field... fields) {
		final Map<String, Field> mappedFields = new TreeMap<>();
		for (final Field field : fields) {
			mappedFields.put(field.getName(), field);
		}
		return mappedFields;
	}

	private static PrimitiveField string(String name) {
		return new PrimitiveField(name, STRING, IS_NOT_ARRAY, IS_PART_OF_KEY);
	}

	private static Field integer(String name) {
		return new PrimitiveField(name, INTEGER, IS_NOT_ARRAY, IS_PART_OF_KEY);
	}

	private static Field geoLocation(String name) {
		return new PrimitiveField(name, GEO_LOCATION, IS_NOT_ARRAY, IS_NOT_PART_OF_KEY);
	}

	private static ReferenceField dataType(String name, String type) {
		return new ReferenceField(name, type, IS_NOT_ARRAY, IS_PART_OF_KEY);
	}

	private static ReferenceField nonKeyDataType(String name, String type) {
		return new ReferenceField(name, type, IS_NOT_ARRAY, IS_NOT_PART_OF_KEY);
	}

	private static ReferenceField dataTypeNonKeyArray(String name, String type) {
		return new ReferenceField(name, type, IS_ARRAY, IS_NOT_PART_OF_KEY);
	}

	private static void createEmailIndexables(IndexableBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.add(selectorType, "send", "Email", "from");
			builder.add(selectorType, "received", "Email", "to");
			builder.add(selectorType, "ccReceived", "Email", "cc");
			builder.add(selectorType, "bccReceived", "Email", "bcc");
		}
	}

	private static void createSmsIndexables(IndexableBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.add(selectorType, "send", "Sms", "sender");
			builder.add(selectorType, "received", "Sms", "receiver");
		}
	}

	private static void createVoiceIndexables(IndexableBuilder builder, String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			builder.add(selectorType, "caller", "Voice", "caller");
			builder.add(selectorType, "called", "Voice", "called");
		}
	}

	private static class IndexableBuilder {
		private final Map<String, List<Indexable>> indexables;

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
			indexables.get(selectorType).add(indexable);
		}

		private void updateIndexableByDocument(String documentType, final Indexable indexable) {
			if (!indexables.containsKey(documentType)) {
				indexables.put(documentType, new LinkedList<>());
			}
			indexables.get(documentType).add(indexable);
		}

		public Map<String, List<Indexable>> build() {
			return indexables;
		}
	}
}