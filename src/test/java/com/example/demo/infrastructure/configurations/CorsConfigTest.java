package com.example.demo.infrastructure.configurations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class CorsConfigTest {

	@Test
	void addCorsMappings_deveConfigurarOrigemMetodosEHeadersPermitidos() {

		var registry = mock(CorsRegistry.class);
		var registration = mock(CorsRegistration.class, RETURNS_SELF);

		when(registry.addMapping("/**")).thenReturn(registration);

		new CorsConfig().addCorsMappings(registry);

		verify(registry).addMapping("/**");
		verify(registration).allowedOrigins("http://localhost:4200");
		verify(registration).allowedMethods("POST", "PUT", "PATCH", "DELETE", "GET");
		verify(registration).allowedHeaders("*");
	}

	@Test
	void extendMessageConverters_deveDesabilitarSerializacaoDeDatasComoTimestamp() {

		var jacksonConverter = new MappingJackson2HttpMessageConverter(new ObjectMapper());
		List<HttpMessageConverter<?>> converters = List.of(jacksonConverter);

		new CorsConfig().extendMessageConverters(converters);

		assertFalse(jacksonConverter.getObjectMapper().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
	}
}
