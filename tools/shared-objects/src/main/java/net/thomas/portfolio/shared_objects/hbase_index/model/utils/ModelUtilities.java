package net.thomas.portfolio.shared_objects.hbase_index.model.utils;

import net.thomas.portfolio.shared_objects.hbase_index.model.utils.DateConverter.Iso8601DateConverter;

public class ModelUtilities {
	private final Iso8601DateConverter iec8601dateConverter;

	public ModelUtilities() {
		iec8601dateConverter = new DateConverter.Iso8601DateConverter();
	}

	/***
	 * @return A thread-safe date converter for parsing and/or formatting dates
	 */
	public DateConverter getIec8601DateConverter() {
		return iec8601dateConverter;
	}
}