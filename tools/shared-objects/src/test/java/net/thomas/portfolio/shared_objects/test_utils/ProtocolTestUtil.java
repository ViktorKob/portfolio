package net.thomas.portfolio.shared_objects.test_utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ProtocolTestUtil {
	private static final ThreadLocal<ObjectMapper> MAPPER = new ThreadLocal<ObjectMapper>() {
		@Override
		public ObjectMapper get() {
			return new ObjectMapper();
		}
	};

	public static final <T> T serializeDeserialize(Object object, Class<? extends T> outputClass) {
		try {
			final ObjectMapper mapper = MAPPER.get();
			final String serializedInstance = mapper.writeValueAsString(object);
			return mapper.readValue(serializedInstance, outputClass);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to serialize / deserialize object for test: " + object, e);
		}
	}
}
