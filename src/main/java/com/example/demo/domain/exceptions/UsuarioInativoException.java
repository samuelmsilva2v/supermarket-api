package com.example.demo.domain.exceptions;

public class UsuarioInativoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UsuarioInativoException() {
		super("Usuário inativo. Entre em contato com um administrador.");
	}
}
