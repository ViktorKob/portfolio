package net.thomas.portfolio.legal.service;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.annotations.Api;

@Controller
@Api(value = "", description = "HTML and JavaScript pages allowing GUI interaction with the legal service")
@EnableConfigurationProperties
public class LegalServiceWebController {
	@Configuration
	static class WebMvcConfig implements WebMvcConfigurer {

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/").setViewName("forward:/history");
		}
	}

	@Secured("ROLE_USER")
	@GetMapping("/history")
	public String getQueryHistory() {
		return "history";
	}
}