package com.example.demo.infrastructure.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.example.demo.domain.models.entities.Perfil;
import com.example.demo.domain.models.entities.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class JwtTokenComponentTest {

	private static final String SECRET_KEY = "468041be-f345-4188-8136-4bdeef74b376";

	private final JwtTokenComponent jwtTokenComponent = new JwtTokenComponent();

	@Test
	void getToken_deveGerarTokenValidoComSubjectEPerfil() {

		var perfil = new Perfil();
		perfil.setId(UUID.randomUUID());
		perfil.setNome("Administrador");

		var usuario = new Usuario();
		usuario.setId(UUID.randomUUID());
		usuario.setUsername("joao.silva");
		usuario.setPerfil(perfil);

		var token = jwtTokenComponent.getToken(usuario);

		assertNotNull(token);
		assertTrue(token.split("\\.").length == 3);

		Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();

		assertEquals("joao.silva", claims.getSubject());
		assertEquals("Administrador", claims.get("perfil", String.class));
		assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
	}
}
