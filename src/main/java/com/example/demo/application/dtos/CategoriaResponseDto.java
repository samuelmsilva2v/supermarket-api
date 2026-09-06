package com.example.demo.application.dtos;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;

@Data
public class CategoriaResponseDto {

	private UUID id;
	private String nome;
	private Instant createdAt;
	private Instant updatedAt;
	private String createdBy;
	private String updatedBy;
}
