package net.thomas.portfolio.common.services.validation;

import java.util.stream.Collectors;

import net.thomas.portfolio.shared_objects.hbase_index.schema.HbaseIndexSchema;

public class DataTypeValidator extends StringPresenceValidator {

	private HbaseIndexSchema schema;
	private String dataTypes;

	public DataTypeValidator(String parameterName, boolean required) {
		super(parameterName, required);
	}

	public void setSchema(HbaseIndexSchema schema) {
		this.schema = schema;
		dataTypes = "[ " + schema.getDataTypes()
			.stream()
			.collect(Collectors.joining(", ")) + " ]";
	}

	@Override
	public boolean isValid(String dataType) {
		return super.isValid(dataType) && (dataType == null || schema.getDataTypes()
			.contains(dataType));
	}

	@Override
	public String getReason(String dataType) {
		if (dataType != null && !dataType.isEmpty() && !schema.getDataTypes()
			.contains(dataType)) {
			return parameterName + " ( was " + dataType + " ) must belong to " + dataTypes;
		} else {
			return super.getReason(dataType);
		}
	}
}