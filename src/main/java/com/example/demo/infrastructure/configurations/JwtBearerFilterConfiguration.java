package com.example.demo.infrastructure.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.infrastructure.filters.JwtBearerFilter;

@Configuration
public class JwtBearerFilterConfiguration {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Bean
	FilterRegistrationBean<JwtBearerFilter> jwtFilter() {

		// Registrando o filtro que irá validar os TOKENS
		FilterRegistrationBean<JwtBearerFilter> filter = new FilterRegistrationBean<JwtBearerFilter>();
		filter.setFilter(new JwtBearerFilter(jwtSecret));

		// Aplicando o filtro em toda a API
		filter.addUrlPatterns("/api/*");

		return filter;
	}

}
