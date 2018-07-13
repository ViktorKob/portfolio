package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import java.util.HashMap;
import java.util.Map;

public class SimpleRepresentationParserLibraryBuilder {
	private final Map<String, SimpleRepresentationParser> parsers;

	public SimpleRepresentationParserLibraryBuilder() {
		parsers = new HashMap<>();
	}

	public void add(SimpleRepresentationParser parser) {
		if (parsers.containsKey(parser.getType())) {
			throw new RuntimeException("Parser for type " + parser.getType() + " was added more than once");
		}
		parsers.put(parser.getType(), parser);
	}

	public SimpleRepresentationParserLibrarySerializable build() {
		return new SimpleRepresentationParserLibrarySerializable(parsers);
	}
}