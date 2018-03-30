package net.thomas.portfolio.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import com.netflix.ribbon.proxy.annotation.Hystrix;

@SpringBootApplication
@EnableDiscoveryClient
@Hystrix
public class SampleServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(SampleServiceApplication.class, args);
	}
}
