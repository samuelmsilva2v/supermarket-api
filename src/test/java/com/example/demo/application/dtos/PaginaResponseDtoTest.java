package com.example.demo.application.dtos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class PaginaResponseDtoTest {

	@Test
	void construtor_deveMapearCamposDaPage() {

		var page = new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 2), 5);

		var dto = new PaginaResponseDto<>(page);

		assertEquals(List.of("a", "b"), dto.getConteudo());
		assertEquals(1, dto.getPaginaAtual());
		assertEquals(2, dto.getTamanhoPagina());
		assertEquals(5, dto.getTotalElementos());
		assertEquals(3, dto.getTotalPaginas());
	}
}
