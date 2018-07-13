package net.thomas.portfolio.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableEurekaServer
@EnableAutoConfiguration
@EnableHystrixDashboard
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
// @EnableAdminServer
public class InfrastructureMasterApplication {
	public static void main(String[] args) {
		SpringApplication.run(InfrastructureMasterApplication.class);
	}
}
