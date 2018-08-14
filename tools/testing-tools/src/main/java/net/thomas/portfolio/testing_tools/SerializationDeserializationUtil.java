package net.thomas.portfolio.testing_tools;

import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildAllPossibleInstancesWithOneFieldSetToNull;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SerializationDeserializationUtil {
	private static final ThreadLocal<ObjectMapper> MAPPER = new ThreadLocal<ObjectMapper>() {
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
		} catch (final Throwable e) {
			throw new RuntimeException("Unable to serialize / deserialize object for test: " + object, e);
		}
	}
}
