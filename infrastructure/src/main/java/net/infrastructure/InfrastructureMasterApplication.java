package net.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
// @EnableHystrixDashboard
public class InfrastructureMasterApplication {
	public static void main(String[] args) {
		SpringApplication.run(InfrastructureMasterApplication.class, args);
	}
}