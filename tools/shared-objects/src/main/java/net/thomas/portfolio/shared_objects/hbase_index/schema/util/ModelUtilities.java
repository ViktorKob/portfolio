package net.thomas.portfolio.shared_objects.hbase_index.schema.util;

import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter;
import net.thomas.portfolio.shared_objects.hbase_index.model.util.DateConverter.SimpleDateConverter;

public class ModelUtilities {
	private final SimpleDateConverter dateConverter;

	public ModelUtilities() {
		dateConverter = new DateConverter.SimpleDateConverter();
	}

	public DateConverter getDateConverter() {
		return dateConverter;
	}
}