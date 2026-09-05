package com.example.demo.domain.services.interfaces;

import java.util.UUID;

import com.example.demo.application.dtos.MovimentacaoEstoqueResponseDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.RegistrarMovimentacaoEstoqueRequestDto;

public interface MovimentacaoEstoqueDomainService {

	public MovimentacaoEstoqueResponseDto registrarMovimentacao(RegistrarMovimentacaoEstoqueRequestDto request,
			String username);

	public PaginaResponseDto<MovimentacaoEstoqueResponseDto> consultarHistoricoPorProduto(UUID produtoId, int pagina,
			int tamanho);

}
