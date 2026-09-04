package com.example.demo.infrastructure.configurations;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.example.demo.infrastructure.filters.JwtBearerFilter;

class JwtBearerFilterConfigurationTest {

	@Test
	void jwtFilter_deveRegistrarFiltroParaTodasRotasDaApi() {

		var registration = new JwtBearerFilterConfiguration().jwtFilter();

		assertInstanceOf(JwtBearerFilter.class, registration.getFilter());
		assertTrue(registration.getUrlPatterns().contains("/api/*"));
	}
}
