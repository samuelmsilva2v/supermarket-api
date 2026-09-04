package com.example.demo.application.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DashboardResponseDtoTest {

	@Test
	void construtor_deveAtribuirNomeCategoriaEQtdProdutos() {

		var dto = new DashboardResponseDto("Bebidas", 5L);

		assertEquals("Bebidas", dto.getNomeCategoria());
		assertEquals(5L, dto.getQtdProdutos());
	}
}
