package com.example.demo.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.demo.domain.models.entities.UnidadeMedida;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProdutoRequestDto {

	@NotBlank(message = "O nome do produto é obrigatório.")
	@Size(max = 100, message = "O nome do produto deve ter no máximo 100 caracteres")
	private String nome;
	
	@NotNull(message = "O preço do produto é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço do produto deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "O preço do produto deve ter no máximo 10 dígitos inteiros e 2 decimais")
	private BigDecimal preco;
	
	@NotNull(message = "A quantidade do produto é obrigatória")
    @Min(value = 0, message = "A quantidade do produto não pode ser negativa")
	private Integer quantidade;

	@NotNull(message = "A unidade de medida do produto é obrigatória")
	private UnidadeMedida unidadeMedida;

	@NotNull(message = "A categoria do produto é obrigatória")
	private UUID categoriaId;
}
