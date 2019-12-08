package net.thomas.portfolio.service_commons.network;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

import net.thomas.portfolio.common.services.parameters.ServiceDependency;
import net.thomas.portfolio.service_commons.network.urls.UrlPrefixBuilder;

public class ServiceDiscoveryUrlPrefixBuilder implements UrlPrefixBuilder {
	private static final Logger LOG = getLogger(ServiceDiscoveryUrlPrefixBuilder.class);
	private static final int MAX_INSTANCE_LOOKUP_ATTEMPTS = 60;

	private final EurekaClient discoveryClient;
	private final ServiceDependency serviceInfo;

	public ServiceDiscoveryUrlPrefixBuilder(final EurekaClient discoveryClient, final ServiceDependency serviceInfo) {
		this.discoveryClient = discoveryClient;
		this.serviceInfo = serviceInfo;
	}

	@Override
	public String build() {
		final String serviceUrl = getServiceInfo(serviceInfo.getName()).getHomePageUrl();
		return serviceUrl.substring(0, serviceUrl.length() - 1);
	}

	private InstanceInfo getServiceInfo(final String serviceName) {
		InstanceInfo instanceInfo = null;
		int tries = 0;
		while (instanceInfo == null && tries < MAX_INSTANCE_LOOKUP_ATTEMPTS) {
			try {
				instanceInfo = discoveryClient.getNextServerFromEureka(serviceName, false);
			} catch (final RuntimeException e) {
				if (e.getMessage().contains("No matches for the virtual host")) {
					LOG.error("Failed discovery of " + serviceInfo.getName() + ". Retrying " + (MAX_INSTANCE_LOOKUP_ATTEMPTS - tries - 1) + " more times.");
					sleepSilently(5000);
				} else {
					throw new ServiceDiscoveryException("Unable to complete service discovery", e);
				}
			}
			tries++;
		}
		if (instanceInfo == null && tries == MAX_INSTANCE_LOOKUP_ATTEMPTS) {
			throw new ServiceDiscoveryException("Unable to locate " + serviceInfo.getName() + " in discovery service");
		} else if (tries > 1) {
			LOG.info("Discovery of " + serviceInfo.getName() + " successful.");
		}
		return instanceInfo;
	}

	private void sleepSilently(int timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (final InterruptedException e1) {
		}
	}

	public static class ServiceDiscoveryException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ServiceDiscoveryException(String message) {
			super(message);
		}

		public ServiceDiscoveryException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}