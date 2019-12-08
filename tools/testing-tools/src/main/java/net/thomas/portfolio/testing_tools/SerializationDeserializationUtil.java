package net.thomas.portfolio.testing_tools;

import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildAllPossibleInstancesWithOneFieldSetToNull;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationDeserializationUtil {
	private static final ThreadLocal<ObjectMapper> MAPPER = new ThreadLocal<>() {
		@Override
		public ObjectMapper get() {
			return new ObjectMapper();
		}
	};

	public static void assertCanSerializeAndDeserializeWithNullValues(Object object) {
		for (final Object instance : buildAllPossibleInstancesWithOneFieldSetToNull(object)) {
			assertEquals(instance, serializeDeserialize(instance));
		}
	}

	public static void assertCanSerializeAndDeserialize(Object object) {
		final Object deserializedObject = serializeDeserialize(object);
		assertEquals(object, deserializedObject);
	}

	@SuppressWarnings("unchecked")
	public static <T> T serializeDeserialize(T object) {
		try {
			final ObjectMapper mapper = MAPPER.get();
			final String serializedInstance = mapper.writeValueAsString(object);
			return (T) mapper.readValue(serializedInstance, object.getClass());
		} catch (final Throwable cause) {
			throw new ProtocolTestException("Unable to serialize / deserialize object for test: " + object, cause);
		}
	}

	public static void assertCanSerializeAndDeserializeAsType(Object object, Class<?> deserializationClass) {
		final Object deserializedObject = serializeDeserializeAsType(object, deserializationClass);
		assertEquals(object, deserializedObject);
	}

	@SuppressWarnings("unchecked")
	public static <T> T serializeDeserializeAsType(T object, Class<?> deserializationClass) {
		try {
			final ObjectMapper mapper = MAPPER.get();
			final String serializedInstance = mapper.writeValueAsString(object);
			return (T) mapper.readValue(serializedInstance, deserializationClass);
		} catch (final Throwable cause) {
			throw new ProtocolTestException("Unable to serialize / deserialize object for test: " + object, cause);
		}
	}

	public static class ProtocolTestException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ProtocolTestException(String message) {
			super(message);
		}

		public ProtocolTestException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
