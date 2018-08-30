package net.thomas.portfolio.nexus.service;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static org.springframework.http.HttpMethod.OPTIONS;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/****
 * Based on
 * https://sandstorm.de/de/blog/post/cors-headers-for-spring-boot-kotlin-webflux-reactor-project.html
 * Added due to problem with cross site scripting requiring authentication
 */
@Configuration
@EnableWebMvc
public class CrossSiteScriptingConfigurer implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HandlerInterceptor() {
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
				System.out.println(request.getMethod());
				if (request.getMethod() == OPTIONS.name()) {
					response.addHeader("Access-Control-Allow-Origin", "*");
					response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
					response.addHeader("Access-Control-Allow-Headers",
							"DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
					response.addHeader("Access-Control-Max-Age", "1728000");
					response.setStatus(SC_NO_CONTENT);
					return false;
				} else {
					return true;
				}
			}
		}).addPathPatterns("/**");
	}
}