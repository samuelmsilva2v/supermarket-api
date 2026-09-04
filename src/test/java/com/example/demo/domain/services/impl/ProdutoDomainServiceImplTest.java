package com.example.demo.domain.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.example.demo.application.dtos.ProdutoRequestDto;
import com.example.demo.application.dtos.ProdutoResponseDto;
import com.example.demo.domain.exceptions.ProdutoComEstoqueException;
import com.example.demo.domain.exceptions.ProdutoComNomeDuplicadoException;
import com.example.demo.domain.models.entities.Categoria;
import com.example.demo.domain.models.entities.Produto;
import com.example.demo.infrastructure.repositories.CategoriaRepository;
import com.example.demo.infrastructure.repositories.ProdutoRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProdutoDomainServiceImplTest {

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private CategoriaRepository categoriaRepository;

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private ProdutoDomainServiceImpl produtoDomainService;

	private ProdutoRequestDto request;
	private Produto produto;
	private Categoria categoria;
	private UUID idProduto;
	private UUID idCategoria;

	@BeforeEach
	void setUp() {
		idProduto = UUID.randomUUID();
		idCategoria = UUID.randomUUID();

		categoria = new Categoria();
		categoria.setId(idCategoria);
		categoria.setNome("Bebidas");

		request = new ProdutoRequestDto();
		request.setNome("Suco de Laranja");
		request.setPreco(BigDecimal.TEN);
		request.setQuantidade(5);
		request.setCategoriaId(idCategoria);

		produto = new Produto();
		produto.setId(idProduto);
		produto.setNome("Suco de Laranja");
		produto.setPreco(BigDecimal.TEN);
		produto.setQuantidade(5);
		produto.setCategoria(categoria);
	}

	@Test
	void registrarProduto_deveLancarExcecao_quandoNomeJaExiste() {

		when(produtoRepository.existsByNome("Suco de Laranja")).thenReturn(true);

		assertThrows(ProdutoComNomeDuplicadoException.class, () -> produtoDomainService.registrarProduto(request));

		verify(produtoRepository, never()).save(any());
	}

	@Test
	void registrarProduto_deveLancarExcecao_quandoCategoriaNaoEncontrada() {

		when(produtoRepository.existsByNome("Suco de Laranja")).thenReturn(false);
		when(categoriaRepository.existsById(idCategoria)).thenReturn(false);

		assertThrows(EntityNotFoundException.class, () -> produtoDomainService.registrarProduto(request));

		verify(produtoRepository, never()).save(any());
	}

	@Test
	void registrarProduto_deveRegistrar_quandoValido() {

		var responseDto = new ProdutoResponseDto();
		responseDto.setNome("Suco de Laranja");

		when(produtoRepository.existsByNome("Suco de Laranja")).thenReturn(false);
		when(categoriaRepository.existsById(idCategoria)).thenReturn(true);
		when(modelMapper.map(request, Produto.class)).thenReturn(produto);
		when(categoriaRepository.findById(idCategoria)).thenReturn(Optional.of(categoria));
		when(modelMapper.map(produto, ProdutoResponseDto.class)).thenReturn(responseDto);

		var response = produtoDomainService.registrarProduto(request);

		assertEquals("Suco de Laranja", response.getNome());
		verify(produtoRepository, times(1)).save(produto);
	}

	@Test
	void atualizarProduto_deveLancarExcecao_quandoNomeJaExisteParaOutroProduto() {

		when(produtoRepository.existsByNomeAndIdNot("Suco de Laranja", idProduto)).thenReturn(true);

		assertThrows(ProdutoComNomeDuplicadoException.class,
				() -> produtoDomainService.atualizarProduto(idProduto, request));

		verify(produtoRepository, never()).save(any());
	}

	@Test
	void atualizarProduto_deveLancarExcecao_quandoProdutoNaoEncontrado() {

		when(produtoRepository.existsByNomeAndIdNot("Suco de Laranja", idProduto)).thenReturn(false);
		when(produtoRepository.findById(idProduto)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class,
				() -> produtoDomainService.atualizarProduto(idProduto, request));
	}

	@Test
	void atualizarProduto_deveLancarExcecao_quandoCategoriaNaoEncontrada() {

		when(produtoRepository.existsByNomeAndIdNot("Suco de Laranja", idProduto)).thenReturn(false);
		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
		when(categoriaRepository.existsById(idCategoria)).thenReturn(false);

		assertThrows(EntityNotFoundException.class,
				() -> produtoDomainService.atualizarProduto(idProduto, request));

		verify(produtoRepository, never()).save(any());
	}

	@Test
	void atualizarProduto_deveAtualizar_quandoValido() {

		var responseDto = new ProdutoResponseDto();
		responseDto.setNome("Suco de Laranja");

		when(produtoRepository.existsByNomeAndIdNot("Suco de Laranja", idProduto)).thenReturn(false);
		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
		when(categoriaRepository.existsById(idCategoria)).thenReturn(true);
		when(categoriaRepository.findById(idCategoria)).thenReturn(Optional.of(categoria));
		when(modelMapper.map(produto, ProdutoResponseDto.class)).thenReturn(responseDto);

		var response = produtoDomainService.atualizarProduto(idProduto, request);

		assertEquals("Suco de Laranja", response.getNome());
		verify(produtoRepository, times(1)).save(produto);
	}

	@Test
	void excluirProduto_deveLancarExcecao_quandoNaoEncontrado() {

		when(produtoRepository.findById(idProduto)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> produtoDomainService.excluirProduto(idProduto));
	}

	@Test
	void excluirProduto_deveLancarExcecao_quandoPossuiEstoque() {

		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));

		assertThrows(ProdutoComEstoqueException.class, () -> produtoDomainService.excluirProduto(idProduto));

		verify(produtoRepository, never()).deleteById(any());
	}

	@Test
	void excluirProduto_deveExcluir_quandoSemEstoque() {

		produto.setQuantidade(0);
		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));

		var resultado = produtoDomainService.excluirProduto(idProduto);

		assertEquals("Produto \"Suco de Laranja\" excluído com sucesso!", resultado);
		verify(produtoRepository, times(1)).deleteById(idProduto);
	}

	@Test
	void consultarProdutoPorId_deveLancarExcecao_quandoNaoEncontrado() {

		when(produtoRepository.existsById(idProduto)).thenReturn(false);

		assertThrows(EntityNotFoundException.class, () -> produtoDomainService.consultarProdutoPorId(idProduto));
	}

	@Test
	void consultarProdutoPorId_deveRetornar_quandoEncontrado() {

		var responseDto = new ProdutoResponseDto();
		responseDto.setNome("Suco de Laranja");

		when(produtoRepository.existsById(idProduto)).thenReturn(true);
		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
		when(modelMapper.map(any(), eq(ProdutoResponseDto.class))).thenReturn(responseDto);

		var response = produtoDomainService.consultarProdutoPorId(idProduto);

		assertEquals("Suco de Laranja", response.getNome());
	}

	@Test
	void consultarProdutos_deveRetornarListaMapeada() {

		var responseDto = new ProdutoResponseDto();
		responseDto.setNome("Suco de Laranja");

		when(produtoRepository.findAll()).thenReturn(List.of(produto));
		when(modelMapper.map(produto, ProdutoResponseDto.class)).thenReturn(responseDto);

		var response = produtoDomainService.consultarProdutos();

		assertEquals(1, response.size());
		assertEquals("Suco de Laranja", response.get(0).getNome());
	}

	@Test
	void consultarProdutoPorNome_deveRetornarPaginaMapeada() {

		var responseDto = new ProdutoResponseDto();
		responseDto.setNome("Suco de Laranja");

		var page = new PageImpl<>(List.of(produto), PageRequest.of(0, 10), 1);

		when(produtoRepository.findByNomeContaining(eq("Suco"), any())).thenReturn(page);
		when(modelMapper.map(produto, ProdutoResponseDto.class)).thenReturn(responseDto);

		var response = produtoDomainService.consultarProdutoPorNome("Suco", 0, 10);

		assertEquals(1, response.getConteudo().size());
		assertEquals("Suco de Laranja", response.getConteudo().get(0).getNome());
		assertEquals(1, response.getTotalElementos());
	}
}
