package com.example.demo.infrastructure.messaging;

import java.io.Serializable;

import lombok.Data;

@Data
public class RedefinicaoSenhaEvento implements Serializable {
	private static final long serialVersionUID = 1L;

	private String nome;
	private String email;
	private String novaSenha;
}
