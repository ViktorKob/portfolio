package net.thomas.portfolio.hbase_index.schema.simple_rep;

public interface SimpleRepresentationParser<PARSE_TYPE, RESULT_TYPE> {

	boolean hasValidFormat(PARSE_TYPE simpleRepresentation);

	RESULT_TYPE parse(String type, PARSE_TYPE simpleRepresentation);
}
