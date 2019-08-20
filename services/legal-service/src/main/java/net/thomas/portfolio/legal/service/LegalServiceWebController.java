package net.thomas.portfolio.legal.service;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.slf4j.LoggerFactory.getLogger;

import org.slf4j.Logger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	}

	@Secured("ROLE_USER")
	@GetMapping("/history")
	public String getQueryHistory(Model model) {
		try {
			model.addAttribute("queryHistory", new ObjectMapper().writeValueAsString(singletonList(singletonMap("key", "value"))));
		} catch (final JsonProcessingException e) {
			LOG.error("Unable to convert history to json", e);
		}
		return "history";
	}
}