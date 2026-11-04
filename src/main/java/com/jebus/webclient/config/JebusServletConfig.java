package com.jebus.webclient.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class JebusServletConfig {
	
	@Bean
	public CorsConfigurationSource corsCofigSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedHeaders(List.of("*"));
		config.setAllowedMethods(List.of("*"));
		config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
		config.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}