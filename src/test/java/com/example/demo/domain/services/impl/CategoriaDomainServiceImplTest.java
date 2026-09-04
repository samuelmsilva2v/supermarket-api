package com.example.demo.domain.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.demo.application.dtos.CategoriaRequestDto;
import com.example.demo.application.dtos.CategoriaResponseDto;
import com.example.demo.application.dtos.DashboardResponseDto;
import com.example.demo.domain.exceptions.CategoriaComNomeDuplicadoException;
import com.example.demo.domain.models.entities.Categoria;
import com.example.demo.infrastructure.repositories.CategoriaRepository;
import com.example.demo.infrastructure.repositories.ProdutoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class CategoriaDomainServiceImplTest {

	@Mock
	private CategoriaRepository categoriaRepository;

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private CategoriaDomainServiceImpl categoriaDomainService;

	private CategoriaRequestDto request;
	private Categoria categoria;
	private UUID id;

	@BeforeEach
	void setUp() {
		id = UUID.randomUUID();

		request = new CategoriaRequestDto();
		request.setNome("Bebidas");

		categoria = new Categoria();
		categoria.setId(id);
		categoria.setNome("Bebidas");
	}

	@Test
	void registrarCategoria_deveLancarExcecao_quandoNomeJaExiste() {

		when(categoriaRepository.existsByNome("Bebidas")).thenReturn(true);

		assertThrows(CategoriaComNomeDuplicadoException.class,
				() -> categoriaDomainService.registrarCategoria(request));

		verify(categoriaRepository, never()).save(any());
	}

	@Test
	void registrarCategoria_deveRegistrar_quandoNomeNaoExiste() {

		var responseDto = new CategoriaResponseDto();
		responseDto.setNome("Bebidas");

		when(categoriaRepository.existsByNome("Bebidas")).thenReturn(false);
		when(modelMapper.map(any(Categoria.class), eq(CategoriaResponseDto.class))).thenReturn(responseDto);

		var response = categoriaDomainService.registrarCategoria(request);

		assertEquals("Bebidas", response.getNome());
		verify(categoriaRepository, times(1)).save(any(Categoria.class));
	}

	@Test
	void editarCategoria_deveLancarExcecao_quandoIdNaoEncontrado() {

		when(categoriaRepository.existsById(id)).thenReturn(false);

		assertThrows(EntityNotFoundException.class, () -> categoriaDomainService.editarCategoria(id, request));

		verify(categoriaRepository, never()).save(any());
	}

	@Test
	void editarCategoria_deveLancarExcecao_quandoNomeJaExiste() {

		when(categoriaRepository.existsById(id)).thenReturn(true);
		when(categoriaRepository.existsByNome("Bebidas")).thenReturn(true);

		assertThrows(CategoriaComNomeDuplicadoException.class,
				() -> categoriaDomainService.editarCategoria(id, request));

		verify(categoriaRepository, never()).save(any());
	}

	@Test
	void editarCategoria_deveAtualizar_quandoValido() {

		var responseDto = new CategoriaResponseDto();
		responseDto.setNome("Bebidas");

		when(categoriaRepository.existsById(id)).thenReturn(true);
		when(categoriaRepository.existsByNome("Bebidas")).thenReturn(false);
		when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
		when(modelMapper.map(any(Categoria.class), eq(CategoriaResponseDto.class))).thenReturn(responseDto);

		var response = categoriaDomainService.editarCategoria(id, request);

		assertEquals("Bebidas", response.getNome());
		verify(categoriaRepository, times(1)).save(categoria);
	}

	@Test
	void excluirCategoria_deveLancarExcecao_quandoIdNaoEncontrado() {

		when(categoriaRepository.findById(id)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> categoriaDomainService.excluirCategoria(id));
	}

	@Test
	void excluirCategoria_deveLancarExcecao_quandoPossuiProdutosAssociados() {

		when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
		when(produtoRepository.existsByCategoriaId(id)).thenReturn(true);

		var ex = assertThrows(RuntimeException.class, () -> categoriaDomainService.excluirCategoria(id));

		assertEquals("Não é possível excluir a categoria \"Bebidas\" pois existem produtos associados a ela.",
				ex.getMessage());
		verify(categoriaRepository, never()).deleteById(any());
	}

	@Test
	void excluirCategoria_deveExcluir_quandoNaoPossuiProdutosAssociados() {

		when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
		when(produtoRepository.existsByCategoriaId(id)).thenReturn(false);

		var resultado = categoriaDomainService.excluirCategoria(id);

		assertEquals("Categoria \"Bebidas\" excluída com sucesso!", resultado);
		verify(categoriaRepository, times(1)).deleteById(id);
	}

	@Test
	void consultarCategoriaPorId_deveLancarExcecao_quandoNaoEncontrada() {

		when(categoriaRepository.existsById(id)).thenReturn(false);

		assertThrows(EntityNotFoundException.class, () -> categoriaDomainService.consultarCategoriaPorId(id));
	}

	@Test
	void consultarCategoriaPorId_deveRetornar_quandoEncontrada() {

		var responseDto = new CategoriaResponseDto();
		responseDto.setNome("Bebidas");

		when(categoriaRepository.existsById(id)).thenReturn(true);
		when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
		when(modelMapper.map(categoria, CategoriaResponseDto.class)).thenReturn(responseDto);

		var response = categoriaDomainService.consultarCategoriaPorId(id);

		assertEquals("Bebidas", response.getNome());
	}

	@Test
	void consultarCategorias_deveRetornarListaMapeada() {

		var responseDto = new CategoriaResponseDto();
		responseDto.setNome("Bebidas");

		when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
		when(modelMapper.map(categoria, CategoriaResponseDto.class)).thenReturn(responseDto);

		var response = categoriaDomainService.consultarCategorias();

		assertEquals(1, response.size());
		assertEquals("Bebidas", response.get(0).getNome());
	}

	@Test
	void consultarCategoriaPorNome_deveRetornarPaginaMapeada() {

		var responseDto = new CategoriaResponseDto();
		responseDto.setNome("Bebidas");

		var page = new PageImpl<>(List.of(categoria), PageRequest.of(0, 10), 1);

		when(categoriaRepository.findByNomeContainingIgnoreCase(eq("Beb"), any())).thenReturn(page);
		when(modelMapper.map(categoria, CategoriaResponseDto.class)).thenReturn(responseDto);

		var response = categoriaDomainService.consultarCategoriaPorNome("Beb", 0, 10);

		assertEquals(1, response.getConteudo().size());
		assertEquals("Bebidas", response.getConteudo().get(0).getNome());
		assertEquals(1, response.getTotalElementos());
	}

	@Test
	void buscarQuantidadePorCategoria_deveRetornarListaDeDashboard() {

		when(categoriaRepository.searchQuantityByCategory())
				.thenReturn(List.of(new DashboardResponseDto("Bebidas", 5L)));

		var response = categoriaDomainService.buscarQuantidadePorCategoria();

		assertEquals(1, response.size());
		assertEquals("Bebidas", response.get(0).getNomeCategoria());
		assertEquals(5L, response.get(0).getQtdProdutos());
	}
}
