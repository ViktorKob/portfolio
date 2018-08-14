package net.thomas.portfolio.shared_objects.test_utils;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.parameters.Parameter;
import net.thomas.portfolio.common.services.parameters.ParameterGroup;

public class ParameterGroupTestUtil {

	public static <T extends ParameterGroup> void assertParametersMatchParameterGroups(T group) {
		try {
			actualAssertParametersMatchParameterGroups(group);
		} catch (final Throwable e) {
			throw new RuntimeException("Unable to serialize object for test: " + group, e);
		}
	}

	private static <T extends ParameterGroup> void actualAssertParametersMatchParameterGroups(T group) throws JsonProcessingException {
		final String serializedForm = new ObjectMapper().writeValueAsString(group);
		for (final Parameter parameter : group.getParameters()) {
			final String message = "Could not find " + parameter + " in " + serializedForm;
			if (parameter.getValue() instanceof Integer || parameter.getValue() instanceof Long) {
				assertTrue(message, serializedForm.contains("\"" + parameter.getName() + "\":" + parameter.getValue()));
			} else {
				assertTrue(message, serializedForm.contains("\"" + parameter.getName() + "\":\"" + parameter.getValue() + "\""));
			}
		}
	}
}