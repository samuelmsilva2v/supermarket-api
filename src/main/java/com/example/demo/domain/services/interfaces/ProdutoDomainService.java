package com.example.demo.domain.services.interfaces;

import java.util.List;
import java.util.UUID;

import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.ProdutoFiltroRequestDto;
import com.example.demo.application.dtos.ProdutoRequestDto;
import com.example.demo.application.dtos.ProdutoResponseDto;

public interface ProdutoDomainService {

	public ProdutoResponseDto registrarProduto(ProdutoRequestDto request);

	public ProdutoResponseDto atualizarProduto(UUID id, ProdutoRequestDto request);

	public String excluirProduto(UUID id);

	public ProdutoResponseDto consultarProdutoPorId(UUID id);

	public PaginaResponseDto<ProdutoResponseDto> consultarProdutosPaginado(ProdutoFiltroRequestDto filtro, int pagina,
			int tamanho);

	public List<ProdutoResponseDto> consultarProdutos();
}
