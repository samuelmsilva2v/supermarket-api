package com.example.demo.application.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.application.dtos.CategoriaRequestDto;
import com.example.demo.application.dtos.CategoriaResponseDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.domain.services.interfaces.CategoriaDomainService;

@ExtendWith(MockitoExtension.class)
class CategoriaControllerTest {

	@Mock
	private CategoriaDomainService categoriaDomainService;

	@InjectMocks
	private CategoriaController categoriaController;

	private UUID id;
	private CategoriaRequestDto request;
	private CategoriaResponseDto response;

	@BeforeEach
	void setUp() {
		id = UUID.randomUUID();
		request = new CategoriaRequestDto();
		request.setNome("Bebidas");
		response = new CategoriaResponseDto();
		response.setId(id);
		response.setNome("Bebidas");
	}

	@Test
	void post_deveRegistrarCategoria() {

		when(categoriaDomainService.registrarCategoria(request)).thenReturn(response);

		var result = categoriaController.post(request);

		assertEquals(response, result);
		verify(categoriaDomainService, times(1)).registrarCategoria(request);
	}

	@Test
	void put_deveAtualizarCategoria() {

		when(categoriaDomainService.editarCategoria(id, request)).thenReturn(response);

		var result = categoriaController.put(id, request);

		assertEquals(response, result);
		verify(categoriaDomainService, times(1)).editarCategoria(id, request);
	}

	@Test
	void delete_deveExcluirCategoria() {

		when(categoriaDomainService.excluirCategoria(id)).thenReturn("Categoria \"Bebidas\" excluída com sucesso!");

		var result = categoriaController.delete(id);

		assertEquals("Categoria \"Bebidas\" excluída com sucesso!", result);
		verify(categoriaDomainService, times(1)).excluirCategoria(id);
	}

	@Test
	void getById_deveConsultarCategoriaPorId() {

		when(categoriaDomainService.consultarCategoriaPorId(id)).thenReturn(response);

		var result = categoriaController.getById(id);

		assertEquals(response, result);
		verify(categoriaDomainService, times(1)).consultarCategoriaPorId(id);
	}

	@Test
	void getAll_deveConsultarTodasCategorias() {

		when(categoriaDomainService.consultarCategorias()).thenReturn(List.of(response));

		var result = categoriaController.getAll();

		assertEquals(1, result.size());
		verify(categoriaDomainService, times(1)).consultarCategorias();
	}

	@Test
	void getByName_deveConsultarCategoriasPorNomePaginado() {

		@SuppressWarnings("unchecked")
		PaginaResponseDto<CategoriaResponseDto> pagina = org.mockito.Mockito.mock(PaginaResponseDto.class);

		when(categoriaDomainService.consultarCategoriaPorNome("Beb", 0, 10)).thenReturn(pagina);

		var result = categoriaController.getByName("Beb", 0, 10);

		assertEquals(pagina, result);
		verify(categoriaDomainService, times(1)).consultarCategoriaPorNome("Beb", 0, 10);
	}
}
