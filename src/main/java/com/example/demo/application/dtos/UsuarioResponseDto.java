package com.example.demo.application.dtos;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class UsuarioResponseDto {

	private UUID id;
	private String nome;
	private String sobrenome;
	private String username;
	private String email;
	private String perfil;
	private boolean ativo;
	private Instant createdAt;
	private Instant updatedAt;
	private String createdBy;
	private String updatedBy;
}
