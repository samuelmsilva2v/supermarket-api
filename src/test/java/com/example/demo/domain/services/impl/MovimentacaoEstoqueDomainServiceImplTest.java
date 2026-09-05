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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.example.demo.application.dtos.RegistrarMovimentacaoEstoqueRequestDto;
import com.example.demo.domain.exceptions.EstoqueInsuficienteException;
import com.example.demo.domain.models.entities.MovimentacaoEstoque;
import com.example.demo.domain.models.entities.Produto;
import com.example.demo.domain.models.entities.TipoMovimentacao;
import com.example.demo.domain.models.entities.Usuario;
import com.example.demo.infrastructure.repositories.MovimentacaoEstoqueRepository;
import com.example.demo.infrastructure.repositories.ProdutoRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueDomainServiceImplTest {

	@Mock
	private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

	@Mock
	private ProdutoRepository produtoRepository;

	@Mock
	private UsuarioRepository usuarioRepository;

	@InjectMocks
	private MovimentacaoEstoqueDomainServiceImpl movimentacaoEstoqueDomainService;

	private Produto produto;
	private Usuario usuario;
	private UUID idProduto;

	@BeforeEach
	void setUp() {
		idProduto = UUID.randomUUID();

		produto = new Produto();
		produto.setId(idProduto);
		produto.setNome("Arroz");
		produto.setQuantidade(10);

		usuario = new Usuario();
		usuario.setNome("João");
		usuario.setSobrenome("Silva");
		usuario.setUsername("joao.silva");
	}

	@Test
	void registrarMovimentacao_deveLancarExcecao_quandoProdutoNaoEncontrado() {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		request.setProdutoId(idProduto);
		request.setTipo(TipoMovimentacao.ENTRADA);
		request.setQuantidade(5);

		when(produtoRepository.findById(idProduto)).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class,
				() -> movimentacaoEstoqueDomainService.registrarMovimentacao(request, "joao.silva"));

		verify(movimentacaoEstoqueRepository, never()).save(any());
	}

	@Test
	void registrarMovimentacao_deveLancarExcecao_quandoSaidaComEstoqueInsuficiente() {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		request.setProdutoId(idProduto);
		request.setTipo(TipoMovimentacao.SAIDA);
		request.setQuantidade(20);

		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));

		assertThrows(EstoqueInsuficienteException.class,
				() -> movimentacaoEstoqueDomainService.registrarMovimentacao(request, "joao.silva"));

		verify(movimentacaoEstoqueRepository, never()).save(any());
		verify(produtoRepository, never()).save(any());
	}

	@Test
	void registrarMovimentacao_deveAumentarEstoque_quandoEntrada() {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		request.setProdutoId(idProduto);
		request.setTipo(TipoMovimentacao.ENTRADA);
		request.setQuantidade(5);
		request.setMotivo("Reposição de fornecedor");

		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(usuario);

		var response = movimentacaoEstoqueDomainService.registrarMovimentacao(request, "joao.silva");

		assertEquals("ENTRADA", response.getTipo().name());
		assertEquals(5, response.getQuantidade());
		assertEquals("Arroz", response.getProdutoNome());
		assertEquals("João Silva", response.getUsuarioNome());
		assertEquals(15, produto.getQuantidade());
		verify(produtoRepository, times(1)).save(produto);
		verify(movimentacaoEstoqueRepository, times(1)).save(any(MovimentacaoEstoque.class));
	}

	@Test
	void registrarMovimentacao_deveDiminuirEstoque_quandoSaida() {

		var request = new RegistrarMovimentacaoEstoqueRequestDto();
		request.setProdutoId(idProduto);
		request.setTipo(TipoMovimentacao.SAIDA);
		request.setQuantidade(4);
		request.setMotivo("Venda no caixa");

		when(produtoRepository.findById(idProduto)).thenReturn(Optional.of(produto));
		when(usuarioRepository.findByUsername("joao.silva")).thenReturn(usuario);

		var response = movimentacaoEstoqueDomainService.registrarMovimentacao(request, "joao.silva");

		assertEquals("SAIDA", response.getTipo().name());
		assertEquals(6, produto.getQuantidade());
		verify(produtoRepository, times(1)).save(produto);
	}

	@Test
	void consultarHistoricoPorProduto_deveLancarExcecao_quandoProdutoNaoEncontrado() {

		when(produtoRepository.existsById(idProduto)).thenReturn(false);

		assertThrows(EntityNotFoundException.class,
				() -> movimentacaoEstoqueDomainService.consultarHistoricoPorProduto(idProduto, 0, 10));
	}

	@Test
	void consultarHistoricoPorProduto_deveRetornarPaginaMapeada() {

		var movimentacao = new MovimentacaoEstoque();
		movimentacao.setId(UUID.randomUUID());
		movimentacao.setProdutoId(produto.getId());
		movimentacao.setProdutoNome(produto.getNome());
		movimentacao.setTipo(TipoMovimentacao.ENTRADA);
		movimentacao.setQuantidade(5);
		movimentacao.setMotivo("Reposição");
		movimentacao.setUsuario(usuario);

		var page = new PageImpl<>(List.of(movimentacao), PageRequest.of(0, 10), 1);

		when(produtoRepository.existsById(idProduto)).thenReturn(true);
		when(movimentacaoEstoqueRepository.findByProdutoId(eq(idProduto), any())).thenReturn(page);

		var response = movimentacaoEstoqueDomainService.consultarHistoricoPorProduto(idProduto, 0, 10);

		assertEquals(1, response.getConteudo().size());
		assertEquals("Arroz", response.getConteudo().get(0).getProdutoNome());
		assertEquals(1, response.getTotalElementos());
	}
}
