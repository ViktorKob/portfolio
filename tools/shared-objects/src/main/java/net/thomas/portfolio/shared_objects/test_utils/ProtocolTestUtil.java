package net.thomas.portfolio.shared_objects.test_utils;

import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildAllPossibleInstancesWithOneFieldSetToNull;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.copyInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;

public class ProtocolTestUtil {
	private static final ThreadLocal<ObjectMapper> MAPPER = new ThreadLocal<ObjectMapper>() {
		@Override
		public ObjectMapper get() {
			return new ObjectMapper();
		}
	};

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

	public static void assertCanSerializeAndDeserializeWithNullValues(Object object) {
		for (final Object instance : buildAllPossibleInstancesWithOneFieldSetToNull(object)) {
			assertEquals(instance, serializeDeserialize(instance));
		}
	}

	public static <T extends ParameterGroup> void assertParametersMatchParameterGroups(T group) {
		try {
			actualAssertParametersMatchParameterGroups(group);
		} catch (final Throwable e) {
			throw new RuntimeException("Unable to serialize object for test: " + group, e);
		}
	}

	private static <T extends ParameterGroup> void actualAssertParametersMatchParameterGroups(T group) throws JsonProcessingException {
		final ObjectMapper mapper = MAPPER.get();
		final String serializedForm = mapper.writeValueAsString(group);
		for (final Parameter parameter : group.getParameters()) {
			final String message = "Could not find " + parameter + " in " + serializedForm;
			if (parameter.getValue() instanceof Integer || parameter.getValue() instanceof Long) {
				assertTrue(message, serializedForm.contains("\"" + parameter.getName() + "\":" + parameter.getValue()));
			} else {
				assertTrue(message, serializedForm.contains("\"" + parameter.getName() + "\":\"" + parameter.getValue() + "\""));
			}
		}
	}

	public static void assertHashCodeIsValid(Object object) {
		assertEquals(object.hashCode(), copyInstance(object).hashCode());
	}

	public static void assertHashCodeIsValidIncludingNullChecks(Object object) {
		assertHashCodeIsValid(object);
		final List<Object> firstInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		final List<Object> secondInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		for (int i = 0; i < firstInstances.size(); i++) {
			assertHashCodeCombinationsWorkAsExpected(object, firstInstances.get(i), secondInstances.get(i));
		}
	}

	private static void assertHashCodeCombinationsWorkAsExpected(Object object, final Object newInstance1, final Object newInstance2) {
		assertNotEquals("Comparisson of hash codes for objects had unexpected outcome for " + object + " against " + newInstance1,
				object.hashCode() == newInstance1.hashCode());
		assertEquals("Comparisson of hash codes for objects had unexpected outcome for " + object + " against " + newInstance2, newInstance1.hashCode(),
				newInstance2.hashCode());
	}

	public static void assertBasicEqualsIsValid(Object object) {
		assertEquals(object, object);
		assertNotEquals(object, null);
		assertNotEquals(object, "");
		assertEquals(object, copyInstance(object));
	}

	public static void assertEqualsIsValidIncludingNullChecks(Object object) {
		assertBasicEqualsIsValid(object);
		final List<Object> firstInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		final List<Object> secondInstances = buildAllPossibleInstancesWithOneFieldSetToNull(object);
		for (int i = 0; i < firstInstances.size(); i++) {
			assertComparisonCombinationsWorkAsExpected(object, firstInstances.get(i), secondInstances.get(i));
		}
	}

	private static void assertComparisonCombinationsWorkAsExpected(Object object, final Object newInstance1, final Object newInstance2) {
		assertFalse("Comparisson of object to different object type had unexpected outcome for " + object + " against " + newInstance1,
				object.equals(newInstance1));
		assertFalse("Comparisson of object to different object type had unexpected outcome for " + newInstance1 + " against " + object,
				newInstance1.equals(object));
		assertTrue("Comparisson of object to different object type had unexpected outcome for " + object + " against " + newInstance2,
				newInstance1.equals(newInstance2));
	}
}