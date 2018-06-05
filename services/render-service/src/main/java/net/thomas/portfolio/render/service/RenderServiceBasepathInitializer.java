package net.thomas.portfolio.render.service;

import static net.thomas.portfolio.services.ServiceGlobals.RENDER_SERVICE_PATH;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.service_commons.properties.ContextPathPropertySource;

@Component
public class RenderServiceBasepathInitializer extends ContextPathPropertySource implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	public RenderServiceBasepathInitializer() {
		super(RENDER_SERVICE_PATH);
	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent environmentEvent) {
		environmentEvent.getEnvironment()
			.getPropertySources()
			.addFirst(this);
	}
}
