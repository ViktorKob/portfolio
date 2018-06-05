package net.thomas.portfolio.analytics.service;

import static net.thomas.portfolio.services.ServiceGlobals.ANALYTICS_SERVICE_PATH;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.service_commons.properties.ContextPathPropertySource;

@Component
public class AnalyticsServiceBasepathInitializer extends ContextPathPropertySource implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	public AnalyticsServiceBasepathInitializer() {
		super(ANALYTICS_SERVICE_PATH);
	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent environmentEvent) {
		environmentEvent.getEnvironment()
			.getPropertySources()
			.addFirst(this);
	}
}
