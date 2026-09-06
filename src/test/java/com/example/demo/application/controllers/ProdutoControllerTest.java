package com.example.demo.application.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.ProdutoRequestDto;
import com.example.demo.application.dtos.ProdutoResponseDto;
import com.example.demo.domain.models.entities.UnidadeMedida;
import com.example.demo.domain.services.interfaces.ProdutoDomainService;

@ExtendWith(MockitoExtension.class)
class ProdutoControllerTest {

	@Mock
	private ProdutoDomainService produtoDomainService;

	@InjectMocks
	private ProdutoController produtoController;

	private UUID id;
	private ProdutoRequestDto request;
	private ProdutoResponseDto response;

	@BeforeEach
	void setUp() {
		id = UUID.randomUUID();
		request = new ProdutoRequestDto();
		request.setNome("Suco de Laranja");
		request.setPreco(BigDecimal.TEN);
		request.setQuantidade(5);
		request.setUnidadeMedida(UnidadeMedida.UNIDADE);
		request.setCategoriaId(UUID.randomUUID());
		response = new ProdutoResponseDto();
		response.setId(id);
		response.setNome("Suco de Laranja");
	}

	@Test
	void post_deveRegistrarProduto() {

		when(produtoDomainService.registrarProduto(request)).thenReturn(response);

		var result = produtoController.post(request);

		assertEquals(response, result);
		verify(produtoDomainService, times(1)).registrarProduto(request);
	}

	@Test
	void put_deveAtualizarProduto() {

		when(produtoDomainService.atualizarProduto(id, request)).thenReturn(response);

		var result = produtoController.put(id, request);

		assertEquals(response, result);
		verify(produtoDomainService, times(1)).atualizarProduto(id, request);
	}

	@Test
	void delete_deveExcluirProduto() {

		when(produtoDomainService.excluirProduto(id)).thenReturn("Produto \"Suco de Laranja\" excluído com sucesso!");

		var result = produtoController.delete(id);

		assertEquals("Produto \"Suco de Laranja\" excluído com sucesso!", result);
		verify(produtoDomainService, times(1)).excluirProduto(id);
	}

	@Test
	void getById_deveConsultarProdutoPorId() {

		when(produtoDomainService.consultarProdutoPorId(id)).thenReturn(response);

		var result = produtoController.getById(id);

		assertEquals(response, result);
		verify(produtoDomainService, times(1)).consultarProdutoPorId(id);
	}

	@Test
	void getAll_deveConsultarTodosProdutos() {

		when(produtoDomainService.consultarProdutos()).thenReturn(List.of(response));

		var result = produtoController.getAll();

		assertEquals(1, result.size());
		verify(produtoDomainService, times(1)).consultarProdutos();
	}

	@Test
	void getByName_deveConsultarProdutosPorNomePaginado() {

		@SuppressWarnings("unchecked")
		PaginaResponseDto<ProdutoResponseDto> pagina = org.mockito.Mockito.mock(PaginaResponseDto.class);

		when(produtoDomainService.consultarProdutoPorNome("Suco", 0, 10)).thenReturn(pagina);

		var result = produtoController.getByName("Suco", 0, 10);

		assertEquals(pagina, result);
		verify(produtoDomainService, times(1)).consultarProdutoPorNome("Suco", 0, 10);
	}
}
