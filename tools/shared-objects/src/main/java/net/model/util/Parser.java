package net.model.util;

public interface Parser<PARSE_TYPE, RESULT_TYPE> {

	RESULT_TYPE parse(String type, PARSE_TYPE source);
}
