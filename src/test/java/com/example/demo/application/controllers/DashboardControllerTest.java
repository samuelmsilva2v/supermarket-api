package com.example.demo.application.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.dtos.DashboardResponseDto;
import com.example.demo.domain.services.interfaces.CategoriaDomainService;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

	@Mock
	private CategoriaDomainService categoriaDomainService;

	@InjectMocks
	private DashboardController dashboardController;

	@Test
	void produtosCategoria_deveRetornarQuantidadePorCategoria() {

		var dashboardDto = new DashboardResponseDto("Bebidas", 5L);

		when(categoriaDomainService.buscarQuantidadePorCategoria()).thenReturn(List.of(dashboardDto));

		var result = dashboardController.produtosCategoria();

		assertEquals(1, result.size());
		assertEquals("Bebidas", result.get(0).getNomeCategoria());
		verify(categoriaDomainService, times(1)).buscarQuantidadePorCategoria();
	}
}
