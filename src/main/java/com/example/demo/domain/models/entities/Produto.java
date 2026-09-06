package com.example.demo.domain.models.entities;

import java.math.BigDecimal;
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
public class Produto {

	@Id
	private UUID id;

	@Column(nullable = false, unique = true)
	private String nome;

	@Column(nullable = false, columnDefinition = "DECIMAL(10, 2)")
	private BigDecimal preco;

	@Column(nullable = false)
	private Integer quantidade;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private UnidadeMedida unidadeMedida;

	@ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
