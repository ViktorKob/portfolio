package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import net.thomas.portfolio.shared_objects.hbase_index.model.DataType;
import net.thomas.portfolio.shared_objects.hbase_index.model.types.Selector;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.Parser;
import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class SimpleRepresentationParserLibrary implements Parser<String, Selector> {
	private final SimpleRepresentationParserLibrary library;
	private final Map<String, SimpleRepresentationParser<String>> parsers;
	private final HbaseIndexSchema model;

	public SimpleRepresentationParserLibrary(HbaseIndexSchema model) {
		this.model = model;
		library = this;
		parsers = new HashMap<>();
		parsers.put("Localname", new SimpleFieldParser("Localname", "name"));
		parsers.put("DisplayedName", new SimpleFieldParser("DisplayedName", "name"));
		parsers.put("PublicId", new SimpleFieldParser("PublicId", "number"));
		parsers.put("PrivateId", new SimpleFieldParser("PrivateId", "number"));
		parsers.put("Domain", new DomainParser());
		parsers.put("EmailAddress", new EmailAddressParser());
	}

	@Override
	public Selector parse(String type, String simpleRepresenation) {
		if (parsers.containsKey(type)) {
			return parsers.get(type)
				.parse(type, simpleRepresenation);
		} else {
			return null;
		}
	}

	private class SimpleFieldParser extends SimpleRepresentationParser<String> {
		private final String field;

		public SimpleFieldParser(String type, String field) {
			super(model.getFieldsForDataType(type));
			this.field = field;
		}

		@Override
		protected void populateValues(DataType entity, String source) {
			entity.put(field, source);
		}
	}

	private class DomainParser extends SimpleRepresentationParser<String> {
		public DomainParser() {
			super(model.getFieldsForDataType("Domain"));
		}

		@Override
		protected void populateValues(DataType entity, String source) {
			if (source.contains(".")) {
				final int firstDot = source.indexOf('.');
				entity.put("domainPart", source.substring(0, firstDot));
				entity.put("domain", library.parse("Domain", source.substring(firstDot + 1)));
			} else {
				entity.put("domainPart", source);
			}
		}
	}

	private class EmailAddressParser extends SimpleRepresentationParser<String> {

		public EmailAddressParser() {
			super(model.getFieldsForDataType("EmailAddress"));
		}

		@Override
		protected void populateValues(DataType entity, String simpleRepresenation) {
			final String[] parts = simpleRepresenation.split("@");
			entity.put("localname", library.parse("Localname", parts[0]));
			entity.put("domain", library.parse("Domain", parts[1]));
		}
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}