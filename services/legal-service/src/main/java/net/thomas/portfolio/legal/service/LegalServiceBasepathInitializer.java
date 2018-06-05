package net.thomas.portfolio.legal.service;

import static net.thomas.portfolio.services.ServiceGlobals.LEGAL_SERVICE_PATH;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.service_commons.properties.ContextPathPropertySource;

@Component
public class LegalServiceBasepathInitializer extends ContextPathPropertySource implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	public LegalServiceBasepathInitializer() {
		super(LEGAL_SERVICE_PATH);
	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent environmentEvent) {
		environmentEvent.getEnvironment()
			.getPropertySources()
			.addFirst(this);
	}
}
