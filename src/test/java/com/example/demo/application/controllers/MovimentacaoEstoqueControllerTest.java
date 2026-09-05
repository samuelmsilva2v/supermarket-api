package com.example.demo.application.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.example.demo.application.dtos.MovimentacaoEstoqueResponseDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.RegistrarMovimentacaoEstoqueRequestDto;
import com.example.demo.domain.services.interfaces.MovimentacaoEstoqueDomainService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueControllerTest {

	@Mock
	private MovimentacaoEstoqueDomainService movimentacaoEstoqueDomainService;

	@Mock
	private HttpServletRequest httpRequest;

	@Mock
	private Claims claims;

	@InjectMocks
	private MovimentacaoEstoqueController movimentacaoEstoqueController;

	@Test
	void post_deveRegistrarMovimentacaoComUsuarioDoToken() {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		var response = new MovimentacaoEstoqueResponseDto();
		response.setId(UUID.randomUUID());

		when(httpRequest.getAttribute("claims")).thenReturn(claims);
		when(claims.getSubject()).thenReturn("joao.silva");
		when(movimentacaoEstoqueDomainService.registrarMovimentacao(request, "joao.silva")).thenReturn(response);

		var result = movimentacaoEstoqueController.post(request, httpRequest);

		assertEquals(response, result);
		verify(movimentacaoEstoqueDomainService, times(1)).registrarMovimentacao(request, "joao.silva");
	}

	@Test
	void getByProduto_deveConsultarHistoricoPaginado() {

		var produtoId = UUID.randomUUID();
		var response = new PaginaResponseDto<MovimentacaoEstoqueResponseDto>(Page.empty());

		when(movimentacaoEstoqueDomainService.consultarHistoricoPorProduto(produtoId, 0, 10)).thenReturn(response);

		var result = movimentacaoEstoqueController.getByProduto(produtoId, 0, 10);

		assertEquals(response, result);
		verify(movimentacaoEstoqueDomainService, times(1)).consultarHistoricoPorProduto(produtoId, 0, 10);
	}
}
