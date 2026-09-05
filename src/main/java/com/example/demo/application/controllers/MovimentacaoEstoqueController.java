package com.example.demo.application.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.dtos.MovimentacaoEstoqueResponseDto;
import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.RegistrarMovimentacaoEstoqueRequestDto;
import com.example.demo.domain.services.interfaces.MovimentacaoEstoqueDomainService;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movimentacoes-estoque")
public class MovimentacaoEstoqueController {

	@Autowired
	private MovimentacaoEstoqueDomainService movimentacaoEstoqueDomainService;

	@Operation(summary = "Serviço para registrar uma movimentação de estoque (entrada/saída).")
	@PostMapping
	public MovimentacaoEstoqueResponseDto post(@RequestBody @Valid RegistrarMovimentacaoEstoqueRequestDto request,
			HttpServletRequest httpRequest) {
		var claims = (Claims) httpRequest.getAttribute("claims");
		return movimentacaoEstoqueDomainService.registrarMovimentacao(request, claims.getSubject());
	}

	@Operation(summary = "Serviço para consultar o histórico de movimentações de estoque de um produto, paginado.")
	@GetMapping("/produto/{produtoId}")
	public PaginaResponseDto<MovimentacaoEstoqueResponseDto> getByProduto(@PathVariable UUID produtoId,
			@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int tamanho) {
		return movimentacaoEstoqueDomainService.consultarHistoricoPorProduto(produtoId, pagina, tamanho);
	}
}
