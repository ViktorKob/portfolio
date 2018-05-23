package net.thomas.portfolio.hbase_index;

import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter.SimpleDateConverter;

public class GraphQlUtilities {
	private final SimpleDateConverter dateConverter;

	public GraphQlUtilities() {
		dateConverter = new DateConverter.SimpleDateConverter();
	}

	public DateConverter getDateConverter() {
		return dateConverter;
	}
}