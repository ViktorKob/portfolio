package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import static java.util.Collections.emptyMap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleRepresentationParserLibrarySerializable extends SimpleRepresentationParserLibraryImpl {

	public SimpleRepresentationParserLibrarySerializable() {
		super(emptyMap());
	}

	public SimpleRepresentationParserLibrarySerializable(Map<String, SimpleRepresentationParser> parsers) {
		super(parsers);
	}

	public Map<String, SimpleRepresentationParser> getParsers() {
		return parsers;
	}

	public void setParsers(Map<String, SimpleRepresentationParser> parsers) {
		this.parsers = parsers;
	}
}