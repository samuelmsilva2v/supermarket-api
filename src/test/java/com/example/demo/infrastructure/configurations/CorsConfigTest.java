package com.example.demo.infrastructure.configurations;

import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

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
}
