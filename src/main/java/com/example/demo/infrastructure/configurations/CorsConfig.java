package com.example.demo.infrastructure.configurations;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {

		registry.addMapping("/**").allowedOrigins("http://localhost:4200")
				.allowedMethods("POST", "PUT", "PATCH", "DELETE", "GET").allowedHeaders("*");
	}

	// @EnableWebMvc desativa a auto-configuração do Jackson do Spring Boot, então
	// spring.jackson.* não tem efeito e esse ajuste precisa ser feito manualmente aqui.
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

		for (HttpMessageConverter<?> converter : converters) {
			if (converter instanceof MappingJackson2HttpMessageConverter jacksonConverter) {
				jacksonConverter.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			}
		}
	}
}
