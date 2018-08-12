package net.thomas.portfolio.shared_objects.test_utils;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
		for (final Field field : getRelevantFields(object)) {
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
		for (final Field field : getRelevantFields(object)) {
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
			final Object[] arguments = buildArgumentListWithSpecifiedFieldAsNull(object, field);
			final Constructor<?> constructor = getFirstMatchingConstructor(object);
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
		for (final Field field : getRelevantFields(object)) {
			if (Object.class.isAssignableFrom(field.getType())) {
				assertNewInstanceWithFieldAsNullIsNotEqualToOriginal(object, field);
			}
		}
	}

	private static Object copy(Object object) {
		try {
			final Object[] arguments = buildArgumentListForObject(object);
			final Constructor<?> constructor = getFirstMatchingConstructor(object);
			if (constructor != null) {
				return constructor.newInstance(arguments);
			} else {
				throw new RuntimeException("Unable to copy instance " + object);
			}
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Unable to copy instance " + object, e);
		}
	}

	private static void assertNewInstanceWithFieldAsNullIsNotEqualToOriginal(Object object, Field field) {
		try {
			final Object[] arguments = buildArgumentListWithSpecifiedFieldAsNull(object, field);
			final Constructor<?> constructor = getFirstMatchingConstructor(object);
			final Object newInstance1 = constructor.newInstance(arguments);
			final Object newInstance2 = constructor.newInstance(arguments);
			assertFalse("Comparisson of object to different object type had unexpected outcome for " + object + " against " + newInstance1,
					object.equals(newInstance1));
			assertFalse("Comparisson of object to different object type had unexpected outcome for " + newInstance1 + " against " + object,
					newInstance1.equals(object));
			assertTrue("Comparisson of object to different object type had unexpected outcome for " + object + " against " + newInstance2,
					newInstance1.equals(newInstance2));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Unable to compare equality for object " + object, e);
		}
	}

	private static Object[] buildArgumentListForObject(Object object) throws IllegalAccessException {
		return buildArgumentListWithSpecifiedFieldAsNull(object, null);
	}

	private static Object[] buildArgumentListWithSpecifiedFieldAsNull(Object object, Field field) throws IllegalAccessException {
		final Object[] arguments = new Object[getRelevantFields(object).length];
		int argument = 0;
		for (final Field entityField : getRelevantFields(object)) {
			if (entityField.equals(field)) {
				arguments[argument++] = null;
			} else {
				arguments[argument++] = getValue(entityField, object);
			}
		}
		return arguments;
	}

	private static Object getValue(Field entityField, Object object) {
		try {
			if (isPublic(entityField.getModifiers())) {
				return entityField.get(object);
			} else {
				for (final Method method : object.getClass()
					.getDeclaredMethods()) {
					if (method.getName()
						.equalsIgnoreCase("get" + entityField.getName())
							|| method.getName()
								.equalsIgnoreCase(entityField.getName())) {
						return method.invoke(object);
					}
				}
				return null;
			}
		} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException("Unable to get value " + entityField + " from object " + object, e);
		}
	}

	public static Field[] getRelevantFields(Object object) {
		return stream(object.getClass()
			.getDeclaredFields()).filter(field -> !"$jacocoData".equals(field.getName()))
				.filter(field -> !isStatic(field.getModifiers()))
				.toArray(Field[]::new);
	}

	public static void assertToStringIsValid(Object object) {
		try {
			final String asString = object.toString();
			for (final Field field : getRelevantFields(object)) {
				final Object value = getValue(field, object);
				if (value != null) {
					assertTrue(asString.contains(value.toString()));
				}
			}
		} catch (final IllegalArgumentException e) {
			throw new RuntimeException("Unable to compare equality for object " + object, e);
		}
	}

	private static Constructor<?> getFirstMatchingConstructor(Object object) {
		final Constructor<?>[] constructors = object.getClass()
			.getDeclaredConstructors();
		final Field[] fields = getRelevantFields(object);
		constructorLoop: for (final Constructor<?> constructor : constructors) {
			if (constructor.getParameterCount() == fields.length) {
				final java.lang.reflect.Parameter[] parameters = constructor.getParameters();
				for (int i = 0; i < fields.length; i++) {
					if (fields[i].getType() != parameters[i].getType() && !isSameAsPrimitive(fields[i], parameters[i])) {
						continue constructorLoop;
					}
				}
				return constructor;
			}
		}
		return null;
	}

	private static boolean isSameAsPrimitive(final Field field, final java.lang.reflect.Parameter parameter) {
		if (field.getType() == boolean.class && parameter.getType() == Boolean.class) {
			return true;
		} else if (field.getType() == float.class && parameter.getType() == Float.class) {
			return true;
		} else if (field.getType() == double.class && parameter.getType() == Double.class) {
			return true;
		} else if (field.getType() == long.class && parameter.getType() == Long.class) {
			return true;
		} else if (field.getType() == int.class && parameter.getType() == Integer.class) {
			return true;
		} else {
			return false;
		}
	}
}