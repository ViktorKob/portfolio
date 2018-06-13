package net.thomas.portfolio.hbase_index;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class HbaseIndexingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HbaseIndexingServiceApplication.class, args);
	}
}