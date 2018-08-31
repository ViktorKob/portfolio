package net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.library;

import static java.util.Collections.emptyMap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.thomas.portfolio.shared_objects.hbase_index.schema.simple_rep.parsers.SimpleRepresentationParserImpl;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SimpleRepresentationParserLibrarySerializable extends SimpleRepresentationParserLibraryImpl {

	public SimpleRepresentationParserLibrarySerializable() {
		super(emptyMap());
	}

	public SimpleRepresentationParserLibrarySerializable(Map<String, SimpleRepresentationParserImpl> parsers) {
		super(parsers);
	}

	public Map<String, SimpleRepresentationParserImpl> getParsers() {
		return parsers;
	}

	public void setParsers(Map<String, SimpleRepresentationParserImpl> parsers) {
		this.parsers = parsers;
		for (final SimpleRepresentationParserImpl parser : parsers.values()) {
			parser.setLibrary(this);
		}
	}
}