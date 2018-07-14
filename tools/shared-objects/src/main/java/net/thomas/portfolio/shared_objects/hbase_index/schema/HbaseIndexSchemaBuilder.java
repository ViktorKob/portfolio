package net.thomas.portfolio.shared_objects.hbase_index.schema;

import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.DomainSimpleRepParser.newDomainParser;
import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.EmailAddressSimpleRepParser.newEmailAddressParser;
import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.PositiveIntegerFieldSimpleRepParser.newPositiveIntegerFieldParser;
import static net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.StringFieldSimpleRepParser.newStringFieldParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;
import net.thomas.portfolio.shared_objects.hbase_index.model.meta_data.Indexable;
import net.thomas.portfolio.shared_objects.hbase_index.model.utils.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.schema.util.SimpleRepresentationParserLibraryBuilder;

public class HbaseIndexSchemaBuilder {
	private final HashMap<String, Fields> dataTypeFields;
	private final IndexableBuilder indexableBuilder;
	private final Set<String> documentTypes;
	private final Set<String> selectorTypes;
	private final Set<String> simpleRepresentableTypes;
	private final SimpleRepresentationParserLibraryBuilder parserBuilder;

	public HbaseIndexSchemaBuilder() {
		dataTypeFields = new HashMap<>();
		documentTypes = new HashSet<>();
		selectorTypes = new HashSet<>();
		simpleRepresentableTypes = new HashSet<>();
		indexableBuilder = new IndexableBuilder();
		parserBuilder = new SimpleRepresentationParserLibraryBuilder();
	}

	public HbaseIndexSchemaBuilder addFields(String dataType, Fields fields) {
		dataTypeFields.put(dataType, fields);
		return this;
	}

	public HbaseIndexSchemaBuilder addDocumentTypes(String... documentTypes) {
		for (final String documentType : documentTypes) {
			this.documentTypes.add(documentType);
		}
		return this;
	}

	public HbaseIndexSchemaBuilder addSelectorTypes(String... selectorTypes) {
		for (final String selectorType : selectorTypes) {
			this.selectorTypes.add(selectorType);
		}
		return this;
	}

	public HbaseIndexSchemaBuilder addSimpleRepresentableTypes(String... simpleRepresentableTypes) {
		for (final String simpleRepresentableType : simpleRepresentableTypes) {
			this.simpleRepresentableTypes.add(simpleRepresentableType);
		}
		return this;
	}

	public HbaseIndexSchemaBuilder addIndexable(String selectorType, String path, String documentType, String documentField) {
		indexableBuilder.add(selectorType, path, documentType, documentField);
		return this;
	}

	public HbaseIndexSchema build() {
		final HbaseIndexSchemaImpl schema = new HbaseIndexSchemaImpl();
		schema.setDataTypeFields(dataTypeFields);
		schema.setDocumentTypes(documentTypes);
		schema.setSelectorTypes(selectorTypes);
		schema.setSimpleRepresentableTypes(simpleRepresentableTypes);
		schema.setSimpleRepParsers(parserBuilder.build());
		schema.setIndexables(indexableBuilder.build());
		return schema;
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

	public HbaseIndexSchemaBuilder addStringFieldParser(final String type, final String field) {
		parserBuilder.add(newStringFieldParser(type, field, createIdCalculator(type)));
		return this;
	}

	public HbaseIndexSchemaBuilder addIntegerFieldParser(final String type, final String field) {
		parserBuilder.add(newPositiveIntegerFieldParser(type, field, createIdCalculator(type)));
		return this;
	}

	public HbaseIndexSchemaBuilder addDomainParser() {
		parserBuilder.add(newDomainParser(createIdCalculator("Domain")));
		return this;
	}

	public HbaseIndexSchemaBuilder addEmailAddressParser() {
		parserBuilder.add(newEmailAddressParser(createIdCalculator("EmailAddress")));
		return this;
	}

	private IdCalculator createIdCalculator(final String type) {
		return new IdCalculator(dataTypeFields.get(type), false);
	}
}