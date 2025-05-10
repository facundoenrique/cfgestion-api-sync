package org.api_sync.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

	private final JwtFilter jwtFilter;
	
	public FilterConfig(JwtFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}
	
	@Bean
	public FilterRegistrationBean<JwtFilter> jwtFilterRegistration() {
		FilterRegistrationBean<JwtFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(jwtFilter);
		registration.addUrlPatterns("/empresas/cae/*");
		registration.setOrder(1);
		return registration;
	}
}
