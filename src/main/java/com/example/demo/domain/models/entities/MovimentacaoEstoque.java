package com.example.demo.domain.models.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class MovimentacaoEstoque {

	@Id
	private UUID id;

	@Column(nullable = false)
	private UUID produtoId;

	@Column(nullable = false, length = 100)
	private String produtoNome;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private TipoMovimentacao tipo;

	@Column(nullable = false)
	private Integer quantidade;

	@Column(length = 255)
	private String motivo;

	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@Column(nullable = false)
	private Instant dataHora;
}
