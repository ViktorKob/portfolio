package net.thomas.portfolio.usage_data.service;

import static net.thomas.portfolio.services.ServiceGlobals.USAGE_DATA_SERVICE_PATH;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.service_commons.properties.ContextPathPropertySource;

@Component
public class UsageDataServiceBasepathInitializer extends ContextPathPropertySource implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	public UsageDataServiceBasepathInitializer() {
		super(USAGE_DATA_SERVICE_PATH);
	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent environmentEvent) {
		environmentEvent.getEnvironment()
			.getPropertySources()
			.addFirst(this);
	}
}
