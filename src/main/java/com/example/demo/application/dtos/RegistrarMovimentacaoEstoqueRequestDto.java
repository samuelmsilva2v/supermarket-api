package com.example.demo.application.dtos;

import java.util.UUID;

import com.example.demo.domain.models.entities.TipoMovimentacao;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrarMovimentacaoEstoqueRequestDto {

	@NotNull(message = "Por favor, informe o produto.")
	private UUID produtoId;

	@NotNull(message = "Por favor, informe o tipo de movimentação (ENTRADA ou SAIDA).")
	private TipoMovimentacao tipo;

	@NotNull(message = "Por favor, informe a quantidade.")
	@Min(value = 1, message = "A quantidade deve ser maior que zero.")
	private Integer quantidade;

	@NotEmpty(message = "Por favor, informe o motivo da movimentação.")
	@Size(max = 255, message = "O motivo deve ter no máximo 255 caracteres.")
	private String motivo;
}
