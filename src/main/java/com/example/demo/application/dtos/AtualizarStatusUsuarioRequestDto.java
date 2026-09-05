package com.example.demo.application.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AtualizarStatusUsuarioRequestDto {

	@NotNull(message = "Por favor, informe o status (ativo) do usuário.")
	private Boolean ativo;
}
