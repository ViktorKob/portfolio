package net.thomas.portfolio.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import de.codecentric.boot.admin.config.EnableAdminServer;

@SpringBootApplication
@EnableEurekaServer
@EnableAutoConfiguration
@EnableAdminServer
public class InfrastructureMasterApplication {
	public static void main(String[] args) {
		SpringApplication.run(InfrastructureMasterApplication.class);
	}
}
