package net.thomas.portfolio.hbase_index.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.thomas.portfolio.common.services.ServiceDependency;

@Configuration
@ConfigurationProperties("hbaseIndexingService")
@EnableWebMvc
public class HbaseIndexingServiceConfiguration extends WebMvcConfigurerAdapter {

	private long randomSeed;
	private ServiceDependency legal;

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}

	public ServiceDependency getLegal() {
		return legal;
	}

	public void setLegal(ServiceDependency legal) {
		this.legal = legal;
	}
}