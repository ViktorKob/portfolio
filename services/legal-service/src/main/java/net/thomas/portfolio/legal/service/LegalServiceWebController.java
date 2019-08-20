package net.thomas.portfolio.legal.service;

import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.annotations.Api;

@Controller
@Api(value = "", description = "Interaction with the legal service")
@EnableConfigurationProperties
public class LegalServiceWebController {
	private static final Logger LOG = getLogger(LegalServiceWebController.class);

	@Configuration
	static class WebMvcConfig implements WebMvcConfigurer {

		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/").setViewName("forward:/history");
		}

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			if (!registry.hasMappingForPattern("/webjars/**")) {
				registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
			}
		}
	}

	@Secured("ROLE_USER")
	@GetMapping("/history")
	public String getQueryHistory() {
		return "history";
	}
}