package com.example.demo.application.dtos;

import lombok.Data;

@Data
public class UsuarioFiltroRequestDto {

	private String username;
	private String nome;
	private Boolean ativo;
	private String perfil;
}
