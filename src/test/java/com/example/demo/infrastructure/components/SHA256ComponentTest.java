package com.example.demo.infrastructure.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class SHA256ComponentTest {

	private final SHA256Component sha256Component = new SHA256Component();

	@Test
	void encrypt_deveGerarHashSha256Consistente() {

		var hash = sha256Component.encrypt("Senha@123");

		assertEquals(64, hash.length());
		assertEquals(hash, sha256Component.encrypt("Senha@123"));
	}

	@Test
	void encrypt_deveGerarHashesDiferentesParaValoresDiferentes() {

		var hash1 = sha256Component.encrypt("Senha@123");
		var hash2 = sha256Component.encrypt("OutraSenha@456");

		assertNotEquals(hash1, hash2);
	}

	@Test
	void encrypt_deveGerarHashConhecido() {

		// Valor SHA-256 de referência para a string vazia.
		var hash = sha256Component.encrypt("");

		assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", hash);
	}
}
