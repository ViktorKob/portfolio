package net.thomas.portfolio.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import net.thomas.portfolio.sample.service.SampleService;

@SpringBootApplication
@EnableDiscoveryClient
// @Hystrix
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SampleServiceApplication implements CommandLineRunner {

	@Autowired
	private SampleService service;

	@Override
	public void run(String... args) {
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken("user", "password", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER")));
		try {
			System.out.println(service.secure());
		} finally {
			SecurityContextHolder.clearContext();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleServiceApplication.class, args);
	}
}