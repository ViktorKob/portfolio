package net.thomas.portfolio.hbase_index.service;

import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import net.thomas.portfolio.service_commons.properties.ContextPathPropertySource;

@Component
public class HbaseIndexingServiceBasepathInitializer extends ContextPathPropertySource implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	public HbaseIndexingServiceBasepathInitializer() {
		super(HBASE_INDEXING_SERVICE_PATH);
	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent environmentEvent) {
		environmentEvent.getEnvironment()
			.getPropertySources()
			.addFirst(this);
	}
}
