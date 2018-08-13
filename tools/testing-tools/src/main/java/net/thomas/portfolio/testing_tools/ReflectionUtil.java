package net.thomas.portfolio.testing_tools;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

	/***
	 * @return Declared fields from object except JaCoCo field added through byte code modification
	 */
	public static Field[] getDeclaredFields(Object object) {
		return stream(object.getClass()
			.getDeclaredFields()).filter(field -> !"$jacocoData".equals(field.getName()))
				.filter(field -> !isStatic(field.getModifiers()))
				.toArray(Field[]::new);
	}

	/***
	 * @return a constructor that can be used to fully initialize the object using the field values or null if not such constructor exists
	 */
	public static Constructor<?> getFirstConstructorMatchingObjectFields(Object object) {
		final Constructor<?>[] constructors = object.getClass()
			.getDeclaredConstructors();
		final Field[] fields = getDeclaredFields(object);
		return locateMatchingConstructor(constructors, fields);
	}

	private static Constructor<?> locateMatchingConstructor(final Constructor<?>[] constructors, final Field[] fields) {
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
		if (field.getType() == float.class && parameter.getType() == Float.class) {
			return true;
		} else if (field.getType() == double.class && parameter.getType() == Double.class) {
			return true;
		} else if (field.getType() == long.class && parameter.getType() == Long.class) {
			return true;
		} else if (field.getType() == int.class && parameter.getType() == Integer.class) {
			return true;
		} else if (field.getType() == boolean.class && parameter.getType() == Boolean.class) {
			return true;
		} else {
			return false;
		}
	}

	/***
	 * @return If field is public -> value is returned using field. If field is not public, but a get<fieldName> method exists -> method is invoked and output
	 *         value is returned. Otherwise, null is returned.
	 */
	public static Object getValue(Field field, Object object) {
		try {
			if (isPublic(field.getModifiers())) {
				return field.get(object);
			} else {
				return getValueUsingMatchingGetMethod(field, object);
			}
		} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException("Unable to get value " + field + " from object " + object, e);
		}
	}

	/***
	 * @return The value from the get method matching (getX, X or isX) the specified field X or null
	 */
	public static Object getValueUsingMatchingGetMethod(Field field, Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : object.getClass()
			.getDeclaredMethods()) {
			if (isSameGetMethod(field, method) || isSame(field, method) || hasSameName(field, method)) {
				return method.invoke(object);
			}
		}
		return null;
	}

	private static boolean isSameGetMethod(Field field, final Method method) {
		return method.getName()
			.equalsIgnoreCase("get" + field.getName());
	}

	private static boolean isSame(Field field, final Method method) {
		return method.getName()
			.equalsIgnoreCase("is" + field.getName());
	}

	private static boolean hasSameName(Field field, final Method method) {
		return method.getName()
			.equalsIgnoreCase(field.getName());
	}

	/***
	 * @return New instances of the object, each constructed with one of its non-primitive fields set to null and the rest to the original value
	 */
	public static List<Object> buildAllPossibleInstancesWithOneFieldSetToNull(Object object) {
		try {
			final List<Object> instances = new ArrayList<>();
			final Constructor<?> constructor = getFirstConstructorMatchingObjectFields(object);
			if (constructor == null) {
				throw new RuntimeException("Unable to locate constructor for object using its fields: " + object);
			}
			for (final Field field : getDeclaredFields(object)) {
				if (Object.class.isAssignableFrom(field.getType())) {
					instances.add(constructor.newInstance(buildValueArrayWithSpecifiedValueAsNull(object, field)));
				}
			}
			return instances;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Unable to construct instances of object using its fields: " + object, e);
		}
	}

	/***
	 * @return The values from the fields in the same order as the fields, or nulls if they could not be extracted
	 */
	public static Object[] buildValueArrayForObject(Object object) {
		return buildValueArrayWithSpecifiedValueAsNull(object, null);
	}

	/***
	 * @return The values from the fields in the same order as the fields, with null for the specified field and any that could not be extracted
	 */
	public static Object[] buildValueArrayWithSpecifiedValueAsNull(Object object, Field field) {
		final Object[] values = new Object[getDeclaredFields(object).length];
		int value = 0;
		for (final Field entityField : getDeclaredFields(object)) {
			if (entityField.equals(field)) {
				values[value++] = null;
			} else {
				values[value++] = getValue(entityField, object);
			}
		}
		return values;
	}

	/***
	 * @return A new instance with identical values
	 */
	public static Object copyInstance(Object object) {
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