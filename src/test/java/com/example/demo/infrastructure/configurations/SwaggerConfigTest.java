package com.example.demo.infrastructure.configurations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SwaggerConfigTest {

	@Test
	void customOpenAPI_deveRetornarInformacoesDaApiCorretamente() {

		var openApi = new SwaggerConfig().customOpenAPI();

		assertNotNull(openApi.getInfo());
		assertEquals("Supermarket API", openApi.getInfo().getTitle());
		assertEquals("API RESTful para gerenciamento e controle de produtos.", openApi.getInfo().getDescription());
		assertEquals("1.0.0", openApi.getInfo().getVersion());
		assertEquals("Samuel Maciel da Silva", openApi.getInfo().getContact().getName());
		assertEquals("samuelmsilva@outlook.com.br", openApi.getInfo().getContact().getEmail());
		assertEquals("Apache 2.0", openApi.getInfo().getLicense().getName());
	}
}
