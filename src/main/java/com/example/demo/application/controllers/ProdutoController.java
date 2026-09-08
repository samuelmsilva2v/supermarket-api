package com.example.demo.application.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.dtos.PaginaResponseDto;
import com.example.demo.application.dtos.ProdutoFiltroRequestDto;
import com.example.demo.application.dtos.ProdutoRequestDto;
import com.example.demo.application.dtos.ProdutoResponseDto;
import com.example.demo.domain.services.interfaces.ProdutoDomainService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

	@Autowired
	private ProdutoDomainService produtoDomainService;
	
	@Operation(summary = "Serviço para registrar um produto.")
	@PostMapping
	public ProdutoResponseDto post(@RequestBody @Valid ProdutoRequestDto request) {
		return produtoDomainService.registrarProduto(request);
	}
	
	@Operation(summary = "Serviço para atualizar um produto.")
	@PutMapping("/{id}")
	public ProdutoResponseDto put(@PathVariable UUID id, @RequestBody @Valid ProdutoRequestDto request) {
		return produtoDomainService.atualizarProduto(id, request);
	}
	
	@Operation(summary = "Serviço para excluir um produto.")
	@DeleteMapping("/{id}")
	public String delete(@PathVariable UUID id) {
		return produtoDomainService.excluirProduto(id);
	}
	
	@Operation(summary = "Serviço para consultar um produto por ID.")
	@GetMapping("/{id}")
	public ProdutoResponseDto getById(@PathVariable UUID id) {
		return produtoDomainService.consultarProdutoPorId(id);
	}
	
	@Operation(summary = "Serviço para consultar todos os produtos.")
	@GetMapping
	public List<ProdutoResponseDto> getAll() {
		return produtoDomainService.consultarProdutos();
	}
	
	@Operation(summary = "Serviço para consultar produtos por filtros (nome, preço, quantidade, unidade de medida, categoria), paginado.")
	@GetMapping("/consultar")
	public PaginaResponseDto<ProdutoResponseDto> getByFiltro(
			@ModelAttribute ProdutoFiltroRequestDto filtro,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "10") int tamanho) {
		return produtoDomainService.consultarProdutosPaginado(filtro, pagina, tamanho);
	}
}
