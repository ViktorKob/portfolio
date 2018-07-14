package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

public interface Parser<PARSE_TYPE, RESULT_TYPE> {

	boolean hasValidFormat(PARSE_TYPE source);

	RESULT_TYPE parse(String type, PARSE_TYPE source);
}
