package com.example.demo.domain.models.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Usuario extends Auditable {

	@Id
	private UUID id;

	@Column(length = 50, nullable = false)
	private String nome;

	@Column(length = 50, nullable = false)
	private String sobrenome;

	@Column(length = 50, nullable = false, unique = true)
	private String username;

	@Column(length = 50, nullable = false, unique = true)
	private String email;

	@Column(length = 100, nullable = false)
	private String senha;

	@ManyToOne
	@JoinColumn(name = "perfil_id", nullable = false)
	private Perfil perfil;

	@Column(nullable = false, columnDefinition = "boolean default true")
	private boolean ativo = true;
}
