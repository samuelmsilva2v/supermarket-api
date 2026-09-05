package com.example.demo.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditarUsuarioRequestDto {

	@Size(min = 2, max = 150, message = "O nome deve ter no mínimo 2 caracteres.")
	@NotEmpty(message = "Por favor, informe o nome do usuário.")
	private String nome;

	@Size(min = 2, max = 150, message = "O sobrenome deve ter no mínimo 2 caracteres.")
	@NotEmpty(message = "Por favor, informe o sobrenome do usuário.")
	private String sobrenome;

	@Size(min = 3, max = 50, message = "O username deve ter no mínimo 3 caracteres.")
	@NotEmpty(message = "Por favor, informe o username do usuário.")
	private String username;

	@Email(message = "Por favor, informe um endereço de e-mail válido.")
	@NotEmpty(message = "Por favor, informe o e-mail do usuário.")
	private String email;

	@Pattern(regexp = "^(Administrador|Operador)$", message = "O perfil deve ser Administrador ou Operador.")
	@NotEmpty(message = "Por favor, informe o perfil do usuário.")
	private String perfil;
}
