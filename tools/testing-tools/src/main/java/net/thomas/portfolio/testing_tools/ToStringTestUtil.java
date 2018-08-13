package net.thomas.portfolio.testing_tools;

import static net.thomas.portfolio.testing_tools.ReflectionUtil.getDeclaredFields;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.getValue;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

public class ToStringTestUtil {
	private static final int MAXIMUM_ACCEPTABLE_CHARACTOR_DISTANCE = 4;

	public static void assertToStringContainsAllFieldsFromObject(Object object) {
		try {
			final String asString = object.toString();
			for (final Field field : getDeclaredFields(object)) {
				final Object value = getValue(field, object);
				if (value != null) {
					assertStringContainsNameAndValue(asString, field.getName(), value.toString());
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException("Unable to compare equality for object " + object, e);
		}
	}

	public static void assertStringContainsNameAndValue(String string, String name, String value) {
		final String remainder = string.substring(string.indexOf(name) + name.length());
		final int indexOfValue = remainder.indexOf(value);
		assertTrue(indexOfValue >= 0 && indexOfValue < MAXIMUM_ACCEPTABLE_CHARACTOR_DISTANCE);
	}
}
