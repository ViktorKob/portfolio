package net.thomas.portfolio.nexus.service.test_utils;

import java.lang.reflect.Field;

import org.mockito.ArgumentMatcher;

import net.thomas.portfolio.shared_objects.hbase_index.request.Bounds;

public class BoundMatcher implements ArgumentMatcher<Bounds> {
	private final Field field;
	private final Object value;

	public BoundMatcher(final Field field, final Object value) {
		this.field = field;
		this.value = value;
	}

	public static ArgumentMatcher<Bounds> matches(final Field field, final Object value) {
		return new BoundMatcher(field, value);
	}

	@Override
	public boolean matches(final Bounds bounds) {
		try {
			return value.equals(field.get(bounds));
		} catch (IllegalArgumentException | IllegalAccessException cause) {
			throw new FieldLookupException("Unable to lookup field " + field.getName() + " in " + bounds, cause);
		}
	}

	public static class FieldLookupException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public FieldLookupException(String message) {
			super(message);
		}

		public FieldLookupException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}