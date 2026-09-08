package com.example.demo.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EsqueciSenhaRequestDto {

	@Email(message = "Por favor, informe um endereço de e-mail válido.")
	@NotEmpty(message = "Por favor, informe o seu e-mail.")
	private String email;
}
