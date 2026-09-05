package com.example.demo.domain.services.impl;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.application.dtos.MovimentacaoEstoqueResponseDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.RegistrarMovimentacaoEstoqueRequestDto;
import com.example.demo.domain.exceptions.EstoqueInsuficienteException;
import com.example.demo.domain.models.entities.MovimentacaoEstoque;
import com.example.demo.domain.models.entities.TipoMovimentacao;
import com.example.demo.domain.services.interfaces.MovimentacaoEstoqueDomainService;
import com.example.demo.infrastructure.repositories.MovimentacaoEstoqueRepository;
import com.example.demo.infrastructure.repositories.ProdutoRepository;
import com.example.demo.infrastructure.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class MovimentacaoEstoqueDomainServiceImpl implements MovimentacaoEstoqueDomainService {

	@Autowired
	private MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Override
	public MovimentacaoEstoqueResponseDto registrarMovimentacao(RegistrarMovimentacaoEstoqueRequestDto request,
			String username) {

		var produto = produtoRepository.findById(request.getProdutoId()).orElseThrow(
				() -> new EntityNotFoundException("Produto com ID " + request.getProdutoId() + " não encontrado."));

		if (request.getTipo() == TipoMovimentacao.SAIDA && produto.getQuantidade() < request.getQuantidade())
			throw new EstoqueInsuficienteException(produto.getNome(), produto.getQuantidade(),
					request.getQuantidade());

		var usuario = usuarioRepository.findByUsername(username);

		var movimentacao = new MovimentacaoEstoque();
		movimentacao.setId(UUID.randomUUID());
		movimentacao.setProdutoId(produto.getId());
		movimentacao.setProdutoNome(produto.getNome());
		movimentacao.setTipo(request.getTipo());
		movimentacao.setQuantidade(request.getQuantidade());
		movimentacao.setMotivo(request.getMotivo());
		movimentacao.setUsuario(usuario);
		movimentacao.setDataHora(Instant.now());

		produto.setQuantidade(request.getTipo() == TipoMovimentacao.ENTRADA
				? produto.getQuantidade() + request.getQuantidade()
				: produto.getQuantidade() - request.getQuantidade());

		produtoRepository.save(produto);
		movimentacaoEstoqueRepository.save(movimentacao);

		return toMovimentacaoEstoqueResponseDto(movimentacao);
	}

	@Override
	public PaginaResponseDto<MovimentacaoEstoqueResponseDto> consultarHistoricoPorProduto(UUID produtoId, int pagina,
			int tamanho) {

		if (!produtoRepository.existsById(produtoId))
			throw new EntityNotFoundException("Produto com ID " + produtoId + " não encontrado.");

		var pageable = PageRequest.of(pagina, tamanho, Sort.by("dataHora").descending());
		var movimentacoesPage = movimentacaoEstoqueRepository.findByProdutoId(produtoId, pageable);

		return new PaginaResponseDto<>(movimentacoesPage.map(this::toMovimentacaoEstoqueResponseDto));
	}

	private MovimentacaoEstoqueResponseDto toMovimentacaoEstoqueResponseDto(MovimentacaoEstoque movimentacao) {
		var dto = new MovimentacaoEstoqueResponseDto();
		dto.setId(movimentacao.getId());
		dto.setProdutoId(movimentacao.getProdutoId());
		dto.setProdutoNome(movimentacao.getProdutoNome());
		dto.setTipo(movimentacao.getTipo());
		dto.setQuantidade(movimentacao.getQuantidade());
		dto.setMotivo(movimentacao.getMotivo());
		dto.setUsuarioNome(movimentacao.getUsuario().getNome() + " " + movimentacao.getUsuario().getSobrenome());
		dto.setDataHora(movimentacao.getDataHora());
		return dto;
	}
}
