package net.thomas.portfolio.shared_objects;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.portfolio.common.services.GetParametizableObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Selector extends GetParametizableObject<Selector> {

	private static Map<String, Method> allGetMethods;
	static {
		allGetMethods = GetParametizableObject.getAllGetMethods(Selector.class);
	}

	private String selector_uid;
	private String selector_type;
	private String selector_stringRepresentation;

	public Selector() {
		super(allGetMethods);
	}

	public String getSelector_uid() {
		return selector_uid;
	}

	public void setSelector_uid(String selector_uid) {
		this.selector_uid = selector_uid;
	}

	public String getSelector_type() {
		return selector_type;
	}

	public void setSelector_type(String selector_type) {
		this.selector_type = selector_type;
	}

	public String getSelector_stringRepresentation() {
		return selector_stringRepresentation;
	}

	public void setSelector_stringRepresentation(String selector_stringRepresentation) {
		this.selector_stringRepresentation = selector_stringRepresentation;
	}

	@Override
	public String toString() {
		return "Selector: ";
	}

	public static Selector fromJson(String asJson) {
		try {
			return new ObjectMapper().readValue(asJson, Selector.class);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to deserialize Selector: " + asJson, e);
		}
	}
}
