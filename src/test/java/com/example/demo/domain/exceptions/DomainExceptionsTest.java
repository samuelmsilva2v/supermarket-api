package com.example.demo.domain.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DomainExceptionsTest {

	@Test
	void categoriaComNomeDuplicadoException_deveConterMensagemComNome() {

		var ex = new CategoriaComNomeDuplicadoException("Bebidas");

		assertEquals("Já existe uma categoria cadastrada com o nome: Bebidas.", ex.getMessage());
	}

	@Test
	void produtoComNomeDuplicadoException_deveConterMensagemComNome() {

		var ex = new ProdutoComNomeDuplicadoException("Arroz");

		assertEquals("Já existe um produto cadastrado com o nome: Arroz.", ex.getMessage());
	}

	@Test
	void produtoComEstoqueException_deveConterMensagemComNomeEQuantidade() {

		var ex = new ProdutoComEstoqueException("Arroz", 7);

		assertEquals("Não é possível excluir o produto 'Arroz' porque ainda possui 7 unidades em estoque.",
				ex.getMessage());
	}

	@Test
	void usuarioComEmailDuplicadoException_deveConterMensagemPadrao() {

		var ex = new UsuarioComEmailDuplicadoException();

		assertEquals("O e-mail informado já está cadastrado, tente outro.", ex.getMessage());
	}

	@Test
	void usuarioComUsernameDuplicadoException_deveConterMensagemPadrao() {

		var ex = new UsuarioComUsernameDuplicadoException();

		assertEquals("O username informado já está cadastrado, tente outro.", ex.getMessage());
	}

	@Test
	void credenciaisInvalidasException_deveConterMensagemPadrao() {

		var ex = new CredenciaisInvalidasException();

		assertEquals("Acesso negado. Usuário não encontrado.", ex.getMessage());
	}

	@Test
	void usuarioInativoException_deveConterMensagemPadrao() {

		var ex = new UsuarioInativoException();

		assertEquals("Usuário inativo. Entre em contato com um administrador.", ex.getMessage());
	}
}
