package net.thomas.portfolio.nexus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class NexusServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(NexusServiceApplication.class, args);
	}
}