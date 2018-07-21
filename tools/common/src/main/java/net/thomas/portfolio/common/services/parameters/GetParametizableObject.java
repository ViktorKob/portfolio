package net.thomas.portfolio.common.services.parameters;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

public class GetParametizableObject<OBJECT_TYPE> {

	protected static Map<String, Method> getAllGetMethods(Class<? extends GetParametizableObject<?>> clazz) {
		final Map<String, Method> methods = new HashMap<>();
		final Field[] fields = clazz.getDeclaredFields();
		for (final Field field : fields) {
			if (!Modifier.isStatic(field.getModifiers())) {
				try {
					methods.put(field.getName(), clazz.getMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1)));
				} catch (NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		return methods;
	}

	private final Map<String, Method> parameterExtractionMethods;

	public GetParametizableObject(Map<String, Method> parameterExtractionMethods) {
		this.parameterExtractionMethods = parameterExtractionMethods;
	}

	public Collection<Parameter> getAsParameterCollection() {
		final Collection<Parameter> parameters = new LinkedList<>();
		for (final Entry<String, Method> entry : parameterExtractionMethods.entrySet()) {
			try {
				parameters.add(new PreSerializedParameter(entry.getKey(), entry.getValue().invoke(this)));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return parameters;
	}
}
