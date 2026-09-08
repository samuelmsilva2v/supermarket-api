package com.example.demo.domain.exceptions;

public class SenhaAtualInvalidaException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SenhaAtualInvalidaException() {
		super("Senha atual inválida.");
	}
}
