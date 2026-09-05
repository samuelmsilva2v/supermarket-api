package com.example.demo.application.dtos;

import java.time.Instant;
import java.util.UUID;

import com.example.demo.domain.models.entities.TipoMovimentacao;

import lombok.Data;

@Data
public class MovimentacaoEstoqueResponseDto {

	private UUID id;
	private UUID produtoId;
	private String produtoNome;
	private TipoMovimentacao tipo;
	private Integer quantidade;
	private String motivo;
	private String usuarioNome;
	private Instant dataHora;
}
