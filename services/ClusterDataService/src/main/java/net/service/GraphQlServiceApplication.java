package net.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import com.netflix.ribbon.proxy.annotation.Hystrix;

@SpringBootApplication
@EnableEurekaClient
@Hystrix
public class GraphQlServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(GraphQlServiceApplication.class, args);
	}
}