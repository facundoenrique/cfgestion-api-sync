package org.api_sync.config;

import org.api_sync.services.usuarios.CustomPasswordEncoder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class FilterConfig {

	private final JwtFilter jwtFilter;
	
	public FilterConfig(JwtFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new CustomPasswordEncoder();
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
