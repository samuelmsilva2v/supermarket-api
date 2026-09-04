package com.example.demo.application.handlers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.exceptions.CategoriaComNomeDuplicadoException;
import com.example.demo.domain.exceptions.CredenciaisInvalidasException;
import com.example.demo.domain.exceptions.ProdutoComEstoqueException;
import com.example.demo.domain.exceptions.ProdutoComNomeDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioComEmailDuplicadoException;
import com.example.demo.domain.exceptions.UsuarioComUsernameDuplicadoException;

import jakarta.persistence.EntityNotFoundException;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	void handleRuntimeException_deveRetornarBadRequest() {

		var response = handler.handleRuntimeException(new RuntimeException("erro genérico"));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("erro genérico", response.getBody());
	}

	@Test
	void handleEntityNotFoundException_deveRetornarNotFound() {

		var response = handler.handleEntityNotFoundException(new EntityNotFoundException("não encontrado"));

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("não encontrado", response.getBody());
	}

	@Test
	void handleResponseStatusException_deveRetornarStatusInformado() {

		var response = handler
				.handleResponseStatusException(new ResponseStatusException(HttpStatus.CONFLICT, "conflito"));

		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
		assertEquals("conflito", response.getBody());
	}

	@Test
	void handleIllegalArgumentException_deveRetornarBadRequest() {

		var response = handler.handleIllegalArgumentException(new IllegalArgumentException("argumento inválido"));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("argumento inválido", response.getBody());
	}

	@Test
	void handleCategoriaComNomeDuplicado_deveRetornarBadRequest() {

		var response = handler.handleCategoriaComNomeDuplicado(new CategoriaComNomeDuplicadoException("Bebidas"));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Já existe uma categoria cadastrada com o nome: Bebidas.", response.getBody());
	}

	@Test
	void handleProdutoComNomeDuplicado_deveRetornarBadRequest() {

		var response = handler.handleProdutoComNomeDuplicado(new ProdutoComNomeDuplicadoException("Arroz"));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Já existe um produto cadastrado com o nome: Arroz.", response.getBody());
	}

	@Test
	void handleProdutoComEstoque_deveRetornarBadRequest() {

		var response = handler.handleProdutoComEstoque(new ProdutoComEstoqueException("Arroz", 3));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Não é possível excluir o produto 'Arroz' porque ainda possui 3 unidades em estoque.",
				response.getBody());
	}

	@Test
	void handleUsuarioComEmailDuplicado_deveRetornarBadRequest() {

		var response = handler.handleUsuarioComEmailDuplicado(new UsuarioComEmailDuplicadoException());

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("O e-mail informado já está cadastrado, tente outro.", response.getBody());
	}

	@Test
	void handleUsuarioComUsernameDuplicado_deveRetornarBadRequest() {

		var response = handler.handleUsuarioComUsernameDuplicado(new UsuarioComUsernameDuplicadoException());

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("O username informado já está cadastrado, tente outro.", response.getBody());
	}

	@Test
	void handleCredenciaisInvalidas_deveRetornarBadRequest() {

		var response = handler.handleCredenciaisInvalidas(new CredenciaisInvalidasException());

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Acesso negado. Usuário não encontrado.", response.getBody());
	}

	@Test
	void handleGenericException_deveRetornarInternalServerError() {

		var response = handler.handleGenericException(new Exception("falha inesperada"));

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Ocorreu um erro inesperado: falha inesperada", response.getBody());
	}
}
