package net.thomas.portfolio.service_commons.properties;

import org.springframework.core.env.PropertySource;

public abstract class ContextPathPropertySource extends PropertySource<String> {

	private static final String SERVER_CONTEXT_PATH = "server.contextPath";
	private final String contextPath;

	public ContextPathPropertySource(String contextPath) {
		super(SERVER_CONTEXT_PATH, contextPath);
		this.contextPath = contextPath;
	}

	@Override
	public Object getProperty(String name) {
		if (SERVER_CONTEXT_PATH.equals(name)) {
			return contextPath;
		} else {
			return null;
		}
	}
}