package net.thomas.portfolio.hbase_index.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ConfigurationProperties("hbaseIndexingService")
@EnableWebMvc
public class HbaseIndexingServiceConfiguration extends WebMvcConfigurerAdapter {

	private long randomSeed;

	public long getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(long randomSeed) {
		this.randomSeed = randomSeed;
	}
}