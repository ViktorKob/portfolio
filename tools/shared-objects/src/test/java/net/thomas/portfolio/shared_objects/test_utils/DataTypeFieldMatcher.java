package net.thomas.portfolio.shared_objects.test_utils;

import static java.util.Collections.singletonMap;

import java.util.Map;
import java.util.Map.Entry;

import org.mockito.ArgumentMatcher;

import net.thomas.portfolio.shared_objects.hbase_index.model.types.DataType;

public class DataTypeFieldMatcher implements ArgumentMatcher<DataType> {
	private final Map<String, Object> expectedValues;

	public static ArgumentMatcher<DataType> matchesField(String field, Object expectedValue) {
		return new DataTypeFieldMatcher(singletonMap(field, expectedValue));
	}

	public static ArgumentMatcher<DataType> matchesFields(Map<String, Object> expectedValues) {
		return new DataTypeFieldMatcher(expectedValues);
	}

	public DataTypeFieldMatcher(Map<String, Object> expectedValues) {
		this.expectedValues = expectedValues;
	}

	@Override
	public boolean matches(DataType entity) {
		for (final Entry<String, Object> entry : expectedValues.entrySet()) {
			if (entryIsMissingOrNotEqual(entity, entry.getKey(), entry.getValue())) {
				return false;
			}
		}
		return true;
	}

	private boolean entryIsMissingOrNotEqual(DataType entity, String fieldName, Object expectedValue) {
		final Object value = entity.get(fieldName);
		return value == null || valueIsDifferent(value, expectedValue);
	}

	private boolean valueIsDifferent(Object value, Object expectedValue) {
		if (expectedValue instanceof DataType) {
			return value == expectedValue;
		} else {
			return !value.equals(expectedValue);
		}
	}
}