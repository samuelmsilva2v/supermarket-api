package com.example.demo.domain.exceptions;

public class EstoqueInsuficienteException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EstoqueInsuficienteException(String nome, Integer quantidadeDisponivel, Integer quantidadeSolicitada) {
		super("Estoque insuficiente para o produto '" + nome + "': disponível " + quantidadeDisponivel
				+ ", solicitado " + quantidadeSolicitada + ".");
	}
}
