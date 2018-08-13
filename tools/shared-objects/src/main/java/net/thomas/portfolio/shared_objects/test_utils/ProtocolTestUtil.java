package net.thomas.portfolio.shared_objects.test_utils;

import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildAllPossibleInstancesWithOneFieldSetToNull;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildValueArrayForObject;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.buildValueArrayWithSpecifiedValueAsNull;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.getDeclaredFields;
import static net.thomas.portfolio.testing_tools.ReflectionUtil.getFirstConstructorMatchingObjectFields;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;

/***
 * TODO[Thomas]: This class should be refactored to a common test project from where the DataType types can be accessed.
 */
public class ProtocolTestUtil {
	private static final ThreadLocal<ObjectMapper> MAPPER = new ThreadLocal<ObjectMapper>() {
		@Override
		public ObjectMapper get() {
			return new ObjectMapper();
		}
	};

	public static <T> void assertCanSerializeAndDeserialize(T object) {
		final T deserializedObject = serializeDeserialize(object);
		assertEquals(object, deserializedObject);
	}

	public static final <T> T serializeDeserialize(T object) {
		try {
			return rawSerializeDeserialize(object);
		} catch (final Throwable e) {
			throw new RuntimeException("Unable to serialize / deserialize object for test: " + object, e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T rawSerializeDeserialize(T object) throws Exception {
		final ObjectMapper mapper = MAPPER.get();
		final String serializedInstance = mapper.writeValueAsString(object);
		return (T) mapper.readValue(serializedInstance, object.getClass());
	}

	public static <T> void assertCanSerializeAndDeserializeWithNullValues(T object) {
		for (final Field field : getDeclaredFields(object)) {
			final T newInstance = createNewInstanceWithFieldSetToNull(object, field);
			final T deserializedObject = serializeDeserialize(newInstance);
			assertEquals(newInstance, deserializedObject);
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
		assertEquals(object.hashCode(), copy(object).hashCode());
	}

	public static void assertHashCodeIsValidIncludingNullChecks(Object object) {
		assertHashCodeIsValid(object);
		for (final Field field : getDeclaredFields(object)) {
			assertInstanceWithFieldAsNullCanCalculateHashCode(object, field);
		}
	}

	private static void assertInstanceWithFieldAsNullCanCalculateHashCode(Object object, Field field) {
		final Object newInstance = createNewInstanceWithFieldSetToNull(object, field);
		assertNotEquals("Calculation of hashcode for entity returned zero for " + object, 0, newInstance.hashCode());
	}

	@SuppressWarnings("unchecked")
	private static <T> T createNewInstanceWithFieldSetToNull(T object, Field field) {
		try {
			final Object[] arguments = buildValueArrayWithSpecifiedValueAsNull(object, field);
			final Constructor<?> constructor = getFirstConstructorMatchingObjectFields(object);
			return (T) constructor.newInstance(arguments);
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new RuntimeException("Unable to create object with null field " + field.getName() + " for " + object, e);
		}
	}

	public static void assertEqualsIsValid(Object object) {
		assertEquals(object, object);
		assertNotEquals(object, null);
		assertNotEquals(object, "");
		assertEquals(object, copy(object));
	}

	public static void assertEqualsIsValidIncludingNullChecks(Object object) {
		assertEqualsIsValid(object);
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

	private static Object copy(Object object) {
		try {
			final Object[] arguments = buildValueArrayForObject(object);
			final Constructor<?> constructor = getFirstConstructorMatchingObjectFields(object);
			if (constructor != null) {
				return constructor.newInstance(arguments);
			} else {
				throw new RuntimeException("Unable to copy instance " + object);
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Unable to copy instance " + object, e);
		}
	}
}