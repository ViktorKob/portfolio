package net.thomas.portfolio.shared_objects.hbase_index.model.util;

public interface Parser<PARSE_TYPE, RESULT_TYPE> {

	RESULT_TYPE parse(String type, PARSE_TYPE source);
}
