package net.thomas.portfolio.hbase_index;

import static java.lang.System.setProperty;
import static net.thomas.portfolio.services.ServiceGlobals.HBASE_INDEXING_SERVICE_PATH;
import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableEurekaClient
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class HbaseIndexingServiceApplication {
	public static void main(String[] args) {
		setProperty("server.servlet.context-path", HBASE_INDEXING_SERVICE_PATH);
		run(HbaseIndexingServiceApplication.class, args);
	}
}