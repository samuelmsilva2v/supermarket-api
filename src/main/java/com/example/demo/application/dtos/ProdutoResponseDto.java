package com.example.demo.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.demo.domain.models.entities.UnidadeMedida;

import lombok.Data;

@Data
public class ProdutoResponseDto {

	private UUID id;
	private String nome;
	private BigDecimal preco;
	private Integer quantidade;
	private UnidadeMedida unidadeMedida;
	private CategoriaResponseDto categoria;
}
