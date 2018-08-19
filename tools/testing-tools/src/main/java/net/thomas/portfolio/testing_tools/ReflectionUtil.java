package net.thomas.portfolio.testing_tools;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toSet;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReflectionUtil {

	/***
	 * @return Declared fields from object except JaCoCo field added through byte code modification
	 */
	public static Field[] getDeclaredFields(final Object object) {
		return stream(object.getClass().getDeclaredFields()).filter(field -> !"$jacocoData".equals(field.getName()))
				.filter(field -> !isStatic(field.getModifiers()))
				.toArray(Field[]::new);
	}

	/***
	 * @return a constructor that can be used to fully initialize the object using the field values or
	 *         null if not such constructor exists
	 */
	public static Constructor<?> getFirstConstructorMatchingObjectFields(final Object object) {
		final Constructor<?>[] constructors = object.getClass().getDeclaredConstructors();
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
	 * @return If field is public -> value is returned using field. If field is not public, but a
	 *         get<fieldName> method exists -> method is invoked and output value is returned.
	 *         Otherwise, null is returned.
	 */
	public static Object getValue(final Field field, final Object object) {
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
	public static Object getValueUsingMatchingGetMethod(final Field field, final Object object) throws IllegalAccessException, InvocationTargetException {
		for (final Method method : object.getClass().getDeclaredMethods()) {
			if (isSameGetMethod(field, method) || isSame(field, method) || hasSameName(field, method)) {
				return method.invoke(object);
			}
		}
		return null;
	}

	private static boolean isSameGetMethod(final Field field, final Method method) {
		return method.getName().equalsIgnoreCase("get" + field.getName());
	}

	private static boolean isSame(final Field field, final Method method) {
		return method.getName().equalsIgnoreCase("is" + field.getName());
	}

	private static boolean hasSameName(final Field field, final Method method) {
		return method.getName().equalsIgnoreCase(field.getName());
	}

	/***
	 * @return New instances of the object, each constructed with one of its non-primitive fields set to
	 *         null and the rest to the original value
	 */
	public static List<Object> buildAllPossibleInstancesWithOneFieldSetToNull(final Object object) {
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
	 * @return The values from the fields in the same order as the fields, or nulls if they could not be
	 *         extracted
	 */
	public static Object[] buildValueArrayForObject(final Object object) {
		return buildValueArrayWithSpecifiedValueAsNull(object, null);
	}

	/***
	 * @return The values from the fields in the same order as the fields, with null for the specified
	 *         field and any that could not be extracted
	 */
	public static Object[] buildValueArrayWithSpecifiedValueAsNull(final Object object, final Field field) {
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
	public static Object copyInstance(final Object object) {
		try {
			// TODO[Thomas]: Experimental solution pending better implementation
			// try {
			// return NameBasedConstructorMatcher.copyInstance(object);
			// } catch (final RuntimeException e) {
			final Object[] arguments = buildValueArrayForObject(object);
			final Constructor<?> constructor = getFirstConstructorMatchingObjectFields(object);
			if (constructor != null) {
				return constructor.newInstance(arguments);
			} else {
				throw new RuntimeException("Unable to copy instance " + object);
			}
			// }
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException("Unable to copy instance " + object, e);
		}
	}

	public static class NameBasedConstructorMatcher {
		/***
		 * @return A new instance with identical values
		 */
		public static Object copyInstance(final Object object) {
			final ConstructorAndAccessors constructor = getBestMatchingConstructor(object);
			if (constructor != null) {
				return constructor.createInstance(object);
			} else {
				throw new RuntimeException("Unable to copy instance " + object);
			}
		}

		/***
		 * @return A new instance with identical values
		 */
		public static Object createCollectionOfInstancesEachWithOneArgumentsAsNull(final Object object) {
			final ConstructorAndAccessors constructor = getBestMatchingConstructor(object);
			if (constructor != null) {
				return constructor.createCollectionOfInstancesEachWithOneParameterSetToNull(object);
			} else {
				throw new RuntimeException("Unable to copy instance " + object);
			}
		}

		private static ConstructorAndAccessors getBestMatchingConstructor(final Object object) {
			final Constructor<?>[] constructors = object.getClass().getDeclaredConstructors();
			ConstructorAndAccessors bestConstructor = null;
			for (final Constructor<?> constructor : constructors) {
				final Map<Parameter, AccessibleObject> parameters = canBeUsedWithObject(constructor, object);
				if (!parameters.isEmpty()) {
					if (bestConstructor == null || bestConstructor.constructor.getParameterCount() < constructor.getParameterCount()) {
						bestConstructor = new ConstructorAndAccessors(constructor, parameters);
					}
				}
			}
			return bestConstructor;
		}

		private static Map<Parameter, AccessibleObject> canBeUsedWithObject(final Constructor<?> constructor, final Object object) {
			final Class<? extends Object> objectClass = object.getClass();
			final Map<Parameter, AccessibleObject> accessors = new LinkedHashMap<>();
			for (final Parameter parameter : constructor.getParameters()) {
				AccessibleObject accessor = getMatchingField(parameter, objectClass);
				if (accessor == null) {
					accessor = getMatchingRetrievalMethod(parameter, objectClass);
				}
				if (accessor != null) {
					accessors.put(parameter, accessor);
				} else {
					return emptyMap();
				}
			}
			return accessors;
		}

		private static Field getMatchingField(final Parameter parameter, final Class<?> objectClass) {
			final Set<Field> matchingFields = stream(objectClass.getFields()).filter(field -> isPublic(field.getModifiers()))
					.filter(field -> field.getName().equalsIgnoreCase(parameter.getName()))
					.collect(toSet());
			if (matchingFields.size() != 1) {
				return null;
			}
			return first(matchingFields);
		}

		private static Method getMatchingRetrievalMethod(final Parameter parameter, final Class<?> objectClass) {
			final String parameterName = parameter.getName();
			final Set<Method> matchingMethods = stream(objectClass.getMethods()).filter(method -> method.getName().endsWith(parameterName)).collect(toSet());
			if (matchingMethods.size() != 1) {
				return null;
			}
			return first(matchingMethods);
		}

		private static <T> T first(final Set<T> elements) {
			return elements.iterator().next();
		}

	}

	public static class ConstructorAndAccessors {
		public final Constructor<?> constructor;
		public final Map<Parameter, AccessibleObject> accessors;

		public ConstructorAndAccessors(final Constructor<?> constructor, final Map<Parameter, AccessibleObject> accessors) {
			this.constructor = constructor;
			this.accessors = accessors;
		}

		public Object createInstance(final Object object) {
			try {
				final Object[] arguments = buildValueArrayWithSpecifiedValueAsNull(object, null);
				return constructor.newInstance(arguments);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Unable to construct new instance og " + object);
			}
		}

		public List<Object> createCollectionOfInstancesEachWithOneParameterSetToNull(final Object object) {
			try {
				final List<Object> instances = new LinkedList<>();
				for (final Parameter parameterToSetToNull : constructor.getParameters()) {
					final Object[] arguments = buildValueArrayWithSpecifiedValueAsNull(object, parameterToSetToNull);
					constructor.newInstance(arguments);
				}
				return instances;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Unable to construct new instance og " + object);
			}
		}

		private Object[] buildValueArrayWithSpecifiedValueAsNull(final Object object, final Parameter parameterToSetToNull)
				throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
			final String nullParameterName = parameterToSetToNull.getName().toLowerCase();
			final Object[] values = new Object[accessors.size()];
			int value = 0;
			for (final AccessibleObject accessor : accessors.values()) {
				if (accessor instanceof Field) {
					final Field field = (Field) accessor;
					if (!field.getName().equalsIgnoreCase(nullParameterName)) {
						values[value++] = field.get(object);
					}
				} else if (accessor instanceof Method) {
					final Method method = (Method) accessor;
					if (!method.getName().toLowerCase().endsWith(nullParameterName)) {
						values[value++] = method.invoke(object);
					}
				}
			}
			return values;
		}
	}
}