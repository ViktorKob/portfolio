package net.thomas.shared_objects;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.thomas.common.services.GetParametizableObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestParameters extends GetParametizableObject<Selector> {

	private static Map<String, Method> allGetMethods;
	static {
		allGetMethods = GetParametizableObject.getAllGetMethods(RequestParameters.class);
	}

	private long requestParameters_initialRow;
	private int requestParameters_rowCount;
	private Long requestParameters_timeLowerBound;
	private Long requestParameters_timeUpperBound;

	public RequestParameters() {
		super(allGetMethods);
		requestParameters_initialRow = 0;
		requestParameters_rowCount = 21;
		requestParameters_timeUpperBound = null;
		requestParameters_timeLowerBound = null;
	}

	public static Map<String, Method> getAllGetMethods() {
		return allGetMethods;
	}

	public static void setAllGetMethods(Map<String, Method> allGetMethods) {
		RequestParameters.allGetMethods = allGetMethods;
	}

	public long getRequestParameters_initialRow() {
		return requestParameters_initialRow;
	}

	public void setRequestParameters_initialRow(long requestParameters_initialRow) {
		this.requestParameters_initialRow = requestParameters_initialRow;
	}

	public int getRequestParameters_rowCount() {
		return requestParameters_rowCount;
	}

	public void setRequestParameters_rowCount(int requestParameters_rowCount) {
		this.requestParameters_rowCount = requestParameters_rowCount;
	}

	public Long getRequestParameters_timeLowerBound() {
		return requestParameters_timeLowerBound;
	}

	public void setRequestParameters_timeLowerBound(Long requestParameters_timeLowerBound) {
		this.requestParameters_timeLowerBound = requestParameters_timeLowerBound;
	}

	public Long getRequestParameters_timeUpperBound() {
		return requestParameters_timeUpperBound;
	}

	public void setRequestParameters_timeUpperBound(Long requestParameters_timeUpperBound) {
		this.requestParameters_timeUpperBound = requestParameters_timeUpperBound;
	}

	@Override
	public String toString() {
		return "RequestParameters: " + getRequestParameters_initialRow() + ", " + getRequestParameters_rowCount() + ", " + getRequestParameters_timeLowerBound()
		+ ", " + getRequestParameters_timeUpperBound();
	}

	public static RequestParameters fromJson(String asJson) {
		try {
			return new ObjectMapper().readValue(asJson, RequestParameters.class);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to deserialize RequestParameters: " + asJson, e);
		}
	}
}
