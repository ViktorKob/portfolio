package net.thomas.portfolio.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class RenderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RenderServiceApplication.class, args);
	}
}