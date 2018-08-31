package net.thomas.portfolio.hbase_index.schema.simple_rep.library;

import static net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.DomainSimpleRepParser.newDomainParser;
import static net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.EmailAddressSimpleRepParser.newEmailAddressParser;
import static net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.PositiveIntegerFieldSimpleRepParser.newPositiveIntegerFieldParser;
import static net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.StringFieldSimpleRepParser.newStringFieldParser;

import java.util.HashMap;
import java.util.Map;

import net.thomas.portfolio.hbase_index.schema.simple_rep.SimpleRepresentationParserLibrary;
import net.thomas.portfolio.hbase_index.schema.simple_rep.parsers.SimpleRepresentationParserImpl;
import net.thomas.portfolio.hbase_index.schema.util.IdCalculator;
import net.thomas.portfolio.shared_objects.hbase_index.model.fields.Fields;

public class SimpleRepresentationParserLibraryBuilder {
	private final Map<String, SimpleRepresentationParserImpl> parsers;
	private final HashMap<String, Fields> dataTypeFields;

	public SimpleRepresentationParserLibraryBuilder() {
		parsers = new HashMap<>();
		dataTypeFields = new HashMap<>();
	}

	public SimpleRepresentationParserLibraryBuilder addFields(String dataType, Fields fields) {
		dataTypeFields.put(dataType, fields);
		return this;
	}

	public SimpleRepresentationParserLibraryBuilder add(SimpleRepresentationParserImpl parser) {
		if (parsers.containsKey(parser.getType())) {
			throw new RuntimeException("Parser for type " + parser.getType() + " was added more than once");
		}
		parsers.put(parser.getType(), parser);
		return this;
	}

	public SimpleRepresentationParserLibrary build() {
		return new SimpleRepresentationParserLibraryImpl(parsers);
	}

	public SimpleRepresentationParserLibraryBuilder addStringFieldParser(final String type, final String field) {
		add(newStringFieldParser(type, field, createIdCalculator(dataTypeFields.get(type))));
		return this;
	}

	public SimpleRepresentationParserLibraryBuilder addIntegerFieldParser(final String type, final String field) {
		add(newPositiveIntegerFieldParser(type, field, createIdCalculator(dataTypeFields.get(type))));
		return this;
	}

	public SimpleRepresentationParserLibraryBuilder addDomainParser() {
		add(newDomainParser(createIdCalculator(dataTypeFields.get("Domain"))));
		return this;
	}

	public SimpleRepresentationParserLibraryBuilder addEmailAddressParser() {
		add(newEmailAddressParser(createIdCalculator(dataTypeFields.get("EmailAddress"))));
		return this;
	}

	private IdCalculator createIdCalculator(Fields fields) {
		return new IdCalculator(fields, false);
	}
}