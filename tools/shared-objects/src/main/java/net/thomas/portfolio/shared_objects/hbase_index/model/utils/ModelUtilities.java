package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter.Iec8601DateConverter;

public class ModelUtilities {
	private final Iec8601DateConverter iec8601dateConverter;

	public ModelUtilities() {
		iec8601dateConverter = new DateConverter.Iec8601DateConverter();
	}

	/***
	 * @return A thread-safe date converter for parsing and/or formatting dates
	 */
	public DateConverter getIec8601DateConverter() {
		return iec8601dateConverter;
	}
}