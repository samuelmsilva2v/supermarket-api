package com.example.demo.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.demo.domain.models.entities.UnidadeMedida;

import lombok.Data;

@Data
public class ProdutoFiltroRequestDto {

	private String nome;
	private BigDecimal precoMin;
	private BigDecimal precoMax;
	private Integer quantidadeMin;
	private Integer quantidadeMax;
	private UnidadeMedida unidadeMedida;
	private UUID categoriaId;
}
